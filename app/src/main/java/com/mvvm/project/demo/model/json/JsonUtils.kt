package com.mvvm.project.demo.model.json

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor
import com.alibaba.fastjson.serializer.SerializerFeature
import java.lang.reflect.Type

object JsonUtils {

    internal val STANDARD_FEATURES = arrayOf(SerializerFeature.DisableCircularReferenceDetect)

    private val FJU = object {

        private val styleParamProcessor: ExtraProcessor = ExtraProcessor { bean, key, value ->
            //可以用来单独解析某一个javabean的某一个字段
//            if (bean is JavaBean) {
//                if ("isLiving" == key) {
//                    bean.isLiving = value.toString()
//                }
//            }
        }

        /**
         * java-object as json-string
         * @param object
         * *
         * @return
         */
        fun seriazileAsString(obj: Any?, vararg features: SerializerFeature): String {
            if (obj == null) {
                return ""
            }
            try {
                return JSON.toJSONString(obj, *features)
            } catch (ex: Exception) {
                throw RuntimeException("Could not write JSON: " + ex.message, ex)
            }

        }

        /**
         * json-string to java-object

         * @param jsonString
         * *
         * @param clazz
         * *
         * @return
         */
        fun <T> deserializeAsObject(jsonString: String?, clazz: Type?): T? {
            if (jsonString == null || clazz == null) {
                return null
            }
            try {
                return JSON.parseObject<T>(jsonString, clazz, styleParamProcessor)
            } catch (ex: Exception) {
                throw RuntimeException("Could not write JSON: " + ex.message, ex)
            }

        }
    }

    /**
     * TODO 目前因为fastjson丢复杂的json时，使用了"$ref"无法正常解析，因此这里禁用了这个优化
     * 如果fastjson修复了，则应该立即启用

     * @param obj
     * *
     * @return
     */
    fun seriazileAsString(obj: Any?): String {
        return FJU.seriazileAsString(obj, *STANDARD_FEATURES)
    }

    /**
     * 标准的序列化接口
     */
    fun seriazileAsStringWithStandard(obj: Any): String {
        return FJU.seriazileAsString(obj, *STANDARD_FEATURES)
    }

    fun <T> deserializeAsObject(jsonString: String, clazz: Type): T {
        return FJU.deserializeAsObject<T>(jsonString, clazz)!!
    }

    //解析数组的
    fun <T> deserializeAsObjectList(jsonString: String?, type: Class<T>): List<T>? {
        if (jsonString == null) {
            return null
        }
        try {
            return JSON.parseArray<T>(jsonString, type)
        } catch (ex: Exception) {
            throw RuntimeException("Could not write JSON: " + ex.message, ex)
        }

    }

    /**
     * 将一个对象，转换成对象

     * @param obj 只能是Object或者Map，不能是数组类型
     * *
     * @return
     */
    fun toJsonMap(obj: Any?): Map<String, Any>? {
        if (obj == null)
            return null
        val objString = JSON.toJSONString(obj)
        return JSON.parseObject<Map<String, Any>>(objString, getType())
    }

    fun toJsonMap(s: String?): Map<String, Any>? {
        if (s == null)
            return null
        return JSON.parseObject<Map<String, Any>>(s, object : TypeReference<Map<String, Any>>() {})
    }

    private fun getType(): Type {
        return object : TypeReference<Map<String, Any>>() {

        }.type
    }
}