package com.mvvm.project.demo.model.net

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mvvm.project.demo.model.net.interceptors.RequestHeaderWrapInterceptor
import com.mvvm.project.demo.common.util.CommonUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.mvvm.project.demo.model.net.suger.FunctionsAndActions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by my on 2018/08/21 0021.
 */

interface RequestCaller {
    /**
     * 获取远程请求的调用者的唯一标识符.
     */
    fun getRequestCallerId(): String

    /**
     * 取消当前的对象发起的所有请求
     */
    fun cancelAllRequest() = Requests.cancelRelatedRequest(this.getRequestCallerId())

    fun onRequestFinished(observableSubscriber: CancelableObservableSubscriber<*>)
//            = Requests.removeFromCacheWhenRequestIsCompleted(this.getRequestCallerId(), observableSubscriber)
            = Requests.removeFromCacheWhenRequestIsCompleted(this.getRequestCallerId(), observableSubscriber)

    fun <T> makeSubscriber(whenSuccess: (responseData: T) -> Unit = {}): CancelableObservableSubscriber<T> {
        return CancelableObservableSubscriber.create<T>(this, Consumer<T> { whenSuccess(it) })
    }

    fun <T> makeSubscriber(whenSuccess: Consumer<T> = Consumer<T> { }): CancelableObservableSubscriber<T> {
        return CancelableObservableSubscriber.create<T>(this, whenSuccess)
    }
}

object Requests {

    private val requestCache: HashMap<String, MutableList<CancelableObservableSubscriber<*>>> = hashMapOf()

    /**
     * 取消所有跟 caller相关的请求的处理
     * @param caller 请求的发起人
     */
    @Synchronized
    fun cancelRelatedRequest(callerId: String) {
        val list: MutableList<CancelableObservableSubscriber<*>>? = requestCache[callerId]
        list?.forEach {
            it.cancel()
        }
        requestCache.remove(callerId)
    }

    /**
     * 取消所有跟 caller相关的请求的处理
     * @param caller 请求的发起人
     */
    @Synchronized
    fun removeFromCacheWhenRequestIsCompleted(callerId: String, subscriber: CancelableObservableSubscriber<*>) {

        val list: MutableList<CancelableObservableSubscriber<*>>? = requestCache[callerId]

        if (list != null) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (subscriber.equals(next)) {
                    subscriber.cancel()
                    iterator.remove()
                    break
                }
            }
            if (!iterator.hasNext()) {
                requestCache.remove(callerId)
            }
        }

    }

    @Synchronized
    fun bindCaller(subscriber: CancelableObservableSubscriber<*>) {
        if (subscriber.requestCaller == null) {
            return
        }
        bindCaller(subscriber.requestCaller, subscriber)
    }

    @Synchronized
    fun bindCaller(callerId: String, subscriber: CancelableObservableSubscriber<*>) {
        val list: MutableList<CancelableObservableSubscriber<*>> = requestCache[callerId] ?: mutableListOf()
        list.add(subscriber)
        requestCache[callerId] = list
    }

    //默认的请求超时时间
    private val DEFAULT_TIMEOUT = 15L
    //图片的默认请求时长
    private val IMG_DEFAULT_TIMEOUT = 15L

    fun getClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(RequestHeaderWrapInterceptor())    //自定义的拦截器
                    .apply {
                        if (CommonUtils.isStethoPresent()) {
                            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))           //日志拦截器
                            addNetworkInterceptor(StethoInterceptor())
                        }
                    }

                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build()

    fun getImageClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .connectTimeout(IMG_DEFAULT_TIMEOUT, TimeUnit.SECONDS).build()

    private val RETROFIT: Retrofit = Retrofit.Builder()
            .client(getClient())//设置客户端
            .baseUrl("http://ic.snssdk.com/")//请求url
            .addConverterFactory(FastJsonConverterFactory.create())//json解析
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))//rxjava支持
//            .addCallAdapterFactory(CallAdapterFactory.create())//rxjava支持
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))//rxjava支持
            .build()

    fun <T> create(targetClass: Class<T>): T = RETROFIT.create(targetClass)

}