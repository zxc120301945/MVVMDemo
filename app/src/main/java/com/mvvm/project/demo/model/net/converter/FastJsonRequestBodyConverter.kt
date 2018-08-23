import com.mvvm.project.demo.model.json.JsonUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import java.io.IOException

/**
 *
 */
class FastJsonRequestBodyConverter<T> : Converter<T, RequestBody> {
    private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val content:String = JsonUtils.seriazileAsString(value)
        return RequestBody.create(MEDIA_TYPE, content)
    }

}
