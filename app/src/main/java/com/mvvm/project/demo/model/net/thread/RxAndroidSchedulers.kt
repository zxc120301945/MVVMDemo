import android.os.SystemClock
import com.mvvm.project.demo.common.util.LogUtils
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private val logger = LogUtils.getLogger("RxAndroidSchedulers")

object ThreadRecycler : Thread() {

    private val mainLock = ReentrantLock()

    val SLEEP_INTERVAL = 25 * 60 * 1000L // 25 分钟一次 ,这个频率是拍脑袋想出来的

    private val cache = ConcurrentHashMap<CancelableTask, CancelableThread>()

    private var keepRunning: Boolean = true //保持运行

    init {
    }

    fun stopAll() {
        keepRunning = false
    }

    //定时清理过多的线程
    override fun run() {
        keepRunning = true
        while (keepRunning) {
            logger.info("清理工作执行之前,先休息下")
            Thread.sleep(SLEEP_INTERVAL)
            logger.info("休息完毕,开始清理.........................")

            val lock = mainLock
            lock.lock()
            try {

                val filter: Map<CancelableTask, CancelableThread> = cache.filter { it.value != null && it.key.shallBeKilled() }
                for (entry in filter) {
                    var thread: CancelableThread? = entry.value
                    logger.info("开始执行清理工作 ... 一共有 ${filter.size} 条需要清理的")
                    cache.remove(entry.key)
                    if (thread != null) {
                        val tryTimes = 0
                        while (!thread.isInterrupted) {
                            if (tryTimes > 6) {
                                logger.info("线程 $thread 多次尝试无法关闭,估计是代码里写了 Thread.sleep() 并且捕获了异常导致这里出错,亟需找到并修改...不然崩溃可期 ....")
                                break
                            }
                            try {
                                thread.interrupt()
                            } catch (ignore: Throwable) {
                                //ignore.printStackTrace()
                            }
                        }
                    }
                    thread = null
                }

            } finally {
                lock.unlock()
            }

        }
    }

    @Synchronized
    fun unRegister(runnable: CancelableTask) {
//        logger.info(" 正确执行完毕一个  ")
        cache.remove(runnable)
    }

    fun register(runnable: CancelableTask, thread: CancelableThread) {
//        logger.info("保存了线程信息,当前保存了 ${cache.size} 条 ")
        cache[runnable] = thread
    }
}

class CancelableThread : Thread {
//    constructor(target: Runnable) : this(null, target, "CancelableThread", 0L)

    private val runnable: Runnable

    constructor(group: ThreadGroup?, target: Runnable, name: String, stackSize: Long) : super(group, target, name, stackSize) {
//        this.isDaemon = true
        runnable = target
    }
//    constructor() : super() {}

//    private val startTime: Long = System.currentTimeMillis()

    override fun run() {
        runnable.run()
    }
}

class CancelableTask(private val runnable: Runnable?) : Runnable {
    var runTask: Boolean = true
    private val startTime: Long = SystemClock.elapsedRealtime()

    fun shallBeKilled(): Boolean {
        return SystemClock.elapsedRealtime() - startTime > ThreadRecycler.SLEEP_INTERVAL
    }

    /**
     * 不再执行
     */
    fun ignoreMe(): Unit {
        runTask = false
    }

    override fun run() {
        if (runTask) {
            runnable?.run()
        }
    }
}


class DefaultThreadFactory : ThreadFactory {
    private val poolNumber = AtomicInteger(1)

    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    init {
        val securityManager = System.getSecurityManager()
        group = if (securityManager != null) {
            securityManager.threadGroup
        } else {
            Thread.currentThread().threadGroup
        }
        namePrefix = "CustomizedIoThread- ${poolNumber.getAndIncrement()} -thread-"
    }

    override fun newThread(runnable: Runnable): Thread {
//        val thread = Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0L)
        val thread = CancelableThread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0L)
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        return thread
    }
}


/**
 * Created by nirack on 17-5-23.
 */
open class AppThreadPoolExecutor @JvmOverloads constructor(corePoolSize: Int, maximumPoolSize: Int = corePoolSize,
                                                           keepAliveTime: Long = 0, unit: TimeUnit = TimeUnit.NANOSECONDS,
                                                           workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>(),
                                                           threadFactory: ThreadFactory = defaultThreadFactory,
                                                           handler: RejectedExecutionHandler = defaultHandler)
    : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler) {

    override fun afterExecute(runnable: Runnable?, throwable: Throwable?) {
//        val currentThread = Thread.currentThread()
        if (runnable != null && runnable is CancelableTask) {
            ThreadRecycler.unRegister(runnable)
        }

        super.afterExecute(runnable, throwable)
//        logger.info("执行任务 结束  ,当前线程数目 ==> ${Thread.activeCount()}")
    }

    override fun execute(command: Runnable?) {
        super.execute(CancelableTask(command))
    }

    override fun beforeExecute(thread: Thread?, runnable: Runnable?) {
//        println("执行任务前  ,当前线程数目 ==> ${Thread.activeCount()}")
        if ((runnable != null && runnable is CancelableTask)
                && (thread != null && thread is CancelableThread && !thread.isInterrupted)) {
            if (runnable.shallBeKilled()) {
                runnable.ignoreMe()
            } else {
                ThreadRecycler.register(runnable, thread)
            }
        }
        super.beforeExecute(thread, runnable)
    }

    companion object {
        private val defaultHandler = ThreadPoolExecutor.AbortPolicy()
        private val defaultThreadFactory = DefaultThreadFactory()
    }
}

//请求以及事件体系使用的线程数的最小和最大值
private val corePoolSize: Int = 36
private val maximumPoolSize: Int = 120

val CustomizedIoScheduler: Scheduler by lazy { Schedulers.from(AppThreadPoolExecutor(corePoolSize, maximumPoolSize)) }
