package moe.gkd.bangumi.http

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.reflect.Type

class JsonOrXmlConverterFactory private constructor() : Converter.Factory() {
    private val xmlFactory = SimpleXmlConverterFactory.create()
    private val jsonFactory = GsonConverterFactory.create()

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return jsonFactory.requestBodyConverter(
            type,
            parameterAnnotations,
            methodAnnotations,
            retrofit
        )
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            if (annotation !is ResponseFormat) continue
            val value = annotation.value
            if (value == ResponseFormat.JSON) {
                return jsonFactory.responseBodyConverter(type, annotations, retrofit)
            } else if (value == ResponseFormat.XML) {
                return xmlFactory.responseBodyConverter(type, annotations, retrofit)
            }
        }
        return jsonFactory.responseBodyConverter(type, annotations, retrofit)
    }

    companion object {
        fun create(): JsonOrXmlConverterFactory {
            return JsonOrXmlConverterFactory()
        }
    }
}