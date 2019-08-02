import com.mvvm.project.demo.model.net.CancelableObservableSubscriber
import com.mvvm.project.demo.model.net.response.ResponseError
import com.mvvm.project.demo.model.net.response.Root
import com.mvvm.project.demo.model.net.suger.RunOnMainSchedulerTransformer
import com.mvvm.project.demo.common.util.ToastUtils
import com.mvvm.project.demo.common.util.reflect.ClassInfo
import com.mvvm.project.demo.common.util.reflect.ReflectUtil
import com.mvvm.project.demo.model.bean.VoidResult
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.List
import java.util.Map
import java.util.Set


private val MSG_DATA_FAILURE = "未能正确的获取数据"
private val REQUEST_RETURN_TYPE_DEF_ERROR = -200
//给各个 接口添加扩展方法
private fun <T> Observable<Root<T>>.afterRequest(intArray: IntArray = intArrayOf()): Observable<T> = this.map {
    return@map mapper(it, intArray)
}

private fun <T> Flowable<Root<T>>.afterRequest(intArray: IntArray = intArrayOf()): Flowable<T> = this.map {
    return@map mapper(it, intArray)
}

private fun <T> Single<Root<T>>.afterRequest(intArray: IntArray = intArrayOf()): Single<T> = this.map {
    return@map mapper(it, intArray)
}

private fun <T> Maybe<Root<T>>.afterRequest(intArray: IntArray = intArrayOf()): Maybe<T> = this.map {
    return@map mapper(it, intArray)
}

// 500 , 1000
private fun <T> mapper(it: Root<T>, intArray: IntArray): T {
//    val code = it.code
    //根据获取到的json，如果业务有直接返回code 那就解析 没有就根据业务其他的判断是否请求成功
    val code = 200

    if (code in intArray && code != 200) {
        throw throw ResponseError(it.code, it.message.toString())
    }
    when (code) {
    // 成功
        200 -> {
            val data: T? = it.data
            if (data == null) {
                val _typeParameter_: Type = it._typeParameter_
                val classInfo: ClassInfo = if (_typeParameter_ is Class<*>) {
                    ReflectUtil.getClassInfo(_typeParameter_)
                } else if (_typeParameter_ is ParameterizedType) {
                    val rawType = _typeParameter_.rawType
                    if (rawType is Class<*>) {
                        ReflectUtil.getClassInfo(rawType)
                    } else {
                        throw ResponseError(REQUEST_RETURN_TYPE_DEF_ERROR, MSG_DATA_FAILURE)
                    }
                } else if (_typeParameter_ is GenericArrayType) {//数组,提前处理了
                    val rawClazz: Class<*> = _typeParameter_.genericComponentType as Class<*>
                    return Array.newInstance(rawClazz, 0) as T
                } else {//其他情况不处理
                    throw ResponseError(REQUEST_RETURN_TYPE_DEF_ERROR, MSG_DATA_FAILURE)
                }

                val voidClass: Class<VoidResult> = VoidResult::class.java
                //如果是 VoidResult ,直接返回
                if (classInfo.isOf(voidClass)) {
                    return VoidResult() as T
                }

                //依次判断是不是集合或者Map
                if (classInfo.isOf(List::class.java)) {
                    return ArrayList<Any>() as T
                } else if (classInfo.isOf(Map::class.java)) {
                    return HashMap<Any, Any>() as T
                } else if (classInfo.isOf(Set::class.java)) {
                    return HashSet<Any>() as T
                } else { //抛出业务异常, -200 ,消息为未能正确获取数据
                    throw ResponseError(REQUEST_RETURN_TYPE_DEF_ERROR, MSG_DATA_FAILURE)
                }
            }
            return it.data
        }

    // 除了200成功返回it外，其他都抛出异常返回false，到errorHandle中去处理
//        ? ->{}
    // 其他系统定义错误代码
        else -> {
            //FIXME 这里可能会报错.......
            ToastUtils.show("异常码：$code")
        }
    }

    throw ResponseError(it.code, it.message.toString())
}

/**
 * 只针对数据正确返回的情况处理
 */
fun <T> Observable<Root<T>>.success(successHandler: Function1<T, Unit>) {
//    this.successOrError(successHandler)
    this.afterRequest().compose(RunOnMainSchedulerTransformer()).subscribe(successHandler, {})
}

/**
 * 啥都不做,只要请求就可以
 */
fun <T> Observable<Root<T>>.whatEver() {
//    this.successOrError(successHandler)
    this.afterRequest().compose(RunOnMainSchedulerTransformer()).subscribe({}, {})
}

//添加处理函数
fun <T> Observable<Root<T>>.handleResponse(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).compose(RunOnMainSchedulerTransformer<T>()).subscribe(observableSubscriber)
}

fun <T> Maybe<Root<T>>.handleResponse(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).compose(RunOnMainSchedulerTransformer<T>()).subscribe(observableSubscriber)
}

fun <T> Single<Root<T>>.handleResponse(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).compose(RunOnMainSchedulerTransformer<T>()).subscribe(observableSubscriber)
}

fun <T> Flowable<Root<T>>.handleResponse(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).compose(RunOnMainSchedulerTransformer<T>()).subscribe(observableSubscriber)
}

//添加处理函数
/*以下函数不做线程切换*/
fun <T> Observable<Root<T>>.handleResponseByDefault(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).subscribe(observableSubscriber)
}

fun <T> Maybe<Root<T>>.handleResponseByDefault(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).subscribe(observableSubscriber)
}

fun <T> Single<Root<T>>.handleResponseByDefault(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).subscribe(observableSubscriber)
}

fun <T> Flowable<Root<T>>.handleResponseByDefault(observableSubscriber: CancelableObservableSubscriber<T>) {
    this.afterRequest(observableSubscriber.specifiedCodes).subscribe(observableSubscriber)
}
/*以上函数不做线程切换*/

// 必须自己定义错误处理函数
fun <T> Observable<Root<T>>.handleWithRawResponse(
        successHandler: Function1<Root<T>, Unit>,
        errorHandler: Function1<Throwable, Unit> = {}) {

//    val afterRequest: Observable<T> = this.afterRequest().compose(RunOnMainSchedulerTransformer<T>())

    this.subscribe(successHandler, errorHandler)

}

// 自定义的网络返回并可以过滤不要的错误码 这种请求与界面没有关联
fun <T> Observable<Root<T>>.handleWithResponse(
        successHandler: Function1<T, Unit>,
        errorHandler: Function1<Throwable, Unit> = {}, specifiedCodes: IntArray = intArrayOf()) {

    val afterRequest: Observable<T> = this.afterRequest(specifiedCodes).compose(RunOnMainSchedulerTransformer<T>())

    afterRequest.subscribe(successHandler, errorHandler)

}