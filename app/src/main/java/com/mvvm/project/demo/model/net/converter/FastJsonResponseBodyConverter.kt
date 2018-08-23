import com.mvvm.project.demo.model.net.response.Root
import com.mvvm.project.demo.model.json.JsonUtils
import com.mvvm.project.demo.common.util.LogUtils
import com.mvvm.project.demo.common.util.ToastUtils
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.SocketTimeoutException

internal class FastJsonResponseBodyConverter<T>(private val type: Type) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        try {
            val jsonString: String = value.string()
//            println("解析数据  类型: $type , 字符串为:  $jsonString")
            var obj = JsonUtils.deserializeAsObject<T>(jsonString, type)
            if(obj != null && obj is Root<*>){
                if(type is ParameterizedType){
                    val rawType: Type = type.actualTypeArguments[0]
                    obj._typeParameter_ = rawType
                }
            }
//            println("转换：${JsonUtil.seriazileAsString(obj)}")
            return obj
        } catch (e: Exception) {
//            e.printStackTrace()
//            ToastHelper.showLong(LingMengApp.getApp(), "转换json错误" + e.message)
            if (e is SocketTimeoutException) {
                ToastUtils.showErrorMessage("当前网络状况不太好哦~_~")
            }
            e.printStackTrace()
//            Log.i("DXC","转换json错误 要转换的 字符串 ==>>> ${value.string()} , ResponseBody对象 ==>>> $value , ${e.printStackTrace()}")
            LogUtils.i("转换json错误 要转换的 字符串 ==>>> ${value.string()} , ResponseBody对象 ==>>> $value , i")
            throw  e

        } finally {
            value.close()
        }
    }
}