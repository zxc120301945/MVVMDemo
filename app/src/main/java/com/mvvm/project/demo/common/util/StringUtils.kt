package com.mvvm.project.demo.common.util

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/**
 * Created by my on 2018/08/21 0021.
 */
object StringUtils {

    /**
     * 生成uuid
     */
    fun uuid(): String = UUID.randomUUID().toString()

    /**
     * 对请求的body进行处理
     */
    fun parseBodyString(bodyString: String): String {
        val result = JSONObject.parseObject(bodyString)
        val listKey = ArrayList(result.keys)
        Collections.sort(listKey)

        var str = ""
        var temp: String
        var value: String
        for (key in listKey) {
            value = result.getString(key)
            if (TextUtils.isEmpty(value)) {
                continue
            }
            temp = key + "=" + (URLEncoder.encode(value, "UTF-8").replace("+", "%20"))
            if ("" == str) {
                str = temp
            } else {
                str += "&" + temp
            }
        }
        return str
    }

    /**
     * 因okhttp3的请求头不允许中文出现的情况，检测非法字符进行encode上传
     * 如果发现非法字符，采用UrlEncode对其进行编码
     */
    fun getValidUA(userAgent: String): String {
        if (TextUtils.isEmpty(userAgent)) {
            return ""
        }
        var validUA = ""
        val uaWithoutLine = userAgent.replace("\n", "")
        var i = 0
        val length = uaWithoutLine.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                try {
                    validUA = URLEncoder.encode(uaWithoutLine, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                return validUA
            }
            i++
        }
        return uaWithoutLine
    }
}