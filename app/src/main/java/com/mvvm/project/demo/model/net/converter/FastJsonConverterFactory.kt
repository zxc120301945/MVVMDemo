import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 *
 */
class FastJsonConverterFactory private constructor() : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit?): Converter<ResponseBody, *> = FastJsonResponseBodyConverter<Any>(type)

    override fun requestBodyConverter(type: Type?,
                                      parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?,
                                      retrofit: Retrofit?): Converter<*, RequestBody>
            = FastJsonRequestBodyConverter<Any>()

    companion object {
        fun create(): FastJsonConverterFactory = FastJsonConverterFactory()
    }

}