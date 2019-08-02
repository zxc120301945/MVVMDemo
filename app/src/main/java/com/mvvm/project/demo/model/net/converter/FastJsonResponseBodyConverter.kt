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
            var obj = JsonUtils.deserializeAsObject<T>(jsonString, type)
            if(obj != null && obj is Root<*>){
                if(type is ParameterizedType){
                    val rawType: Type = type.actualTypeArguments[0]
                    obj._typeParameter_ = rawType
                }
            }
            return obj
        } catch (e: Exception) {
            if (e is SocketTimeoutException) {
                ToastUtils.showErrorMessage("当前网络状况不太好哦~_~")
            }
            e.printStackTrace()
            throw  e

        } finally {
            value.close()
        }
    }
}