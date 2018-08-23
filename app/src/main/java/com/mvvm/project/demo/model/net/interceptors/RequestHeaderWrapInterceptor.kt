package com.mvvm.project.demo.model.net.interceptors

import android.text.TextUtils
import com.mvvm.project.demo.DemoApplication
import com.mvvm.project.demo.R
import com.mvvm.project.demo.common.util.NetWorkUtils
import com.mvvm.project.demo.common.util.StringUtils
import com.mvvm.project.demo.common.util.ToastUtils
import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import java.io.IOException
import java.lang.Exception
import java.net.URLEncoder
import java.nio.charset.Charset

class RequestHeaderWrapInterceptor : Interceptor{
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!NetWorkUtils.isNetConnected()) {
            ToastUtils.show(DemoApplication.getApp().resources.getString(R.string.network_not_available))
        }

        //okhttp3的请求头不允许中文出现的情况,可以先encode一次,StringtUtils.getValidUA()
        val lmInfoParams = StringBuffer()
                .append("t=A")
                .apply {
                    append("&h=${System.currentTimeMillis() / 1000}")

                    if (!android.os.Build.VERSION.RELEASE.isEmpty()) {
                        append("&o=${android.os.Build.VERSION.RELEASE}")//系统版本号
                    }

                    append("&n=${when (NetWorkUtils.getNetworkState()) { //获取当前的网络类型
                        NetWorkUtils.NETWORK_2G, NetWorkUtils.NETWORK_MOBILE -> "2G"
                        NetWorkUtils.NETWORK_3G -> "3G"
                        NetWorkUtils.NETWORK_4G -> "4G"
                        else -> "Wifi"
                    }}")

                    if (!android.os.Build.MANUFACTURER.isEmpty()) {
                        append("&d=${URLEncoder.encode(android.os.Build.MANUFACTURER, "UTF-8")}")//设备名称
                    }
                    if (!android.os.Build.MODEL.isEmpty()) {
                        append("&m=${android.os.Build.MODEL}")//设备型号
                    }

                    var IMSI = ""
                    val operatorId = NetWorkUtils.getOperatorId()
                    if (!TextUtils.isEmpty(operatorId)) {
                        if (operatorId.startsWith("46000") || operatorId.startsWith("46002") || operatorId.startsWith("46007")) {
                            IMSI = "M"//移动
                        } else if (operatorId.startsWith("46001") || operatorId.startsWith("46006")) {
                            IMSI = "U"//联通
                        } else if (operatorId.startsWith("46003")) {
                            IMSI = "T"//电信
                        } else {
                            IMSI = ""
                        }
                    }

                    if (!IMSI.isEmpty()) {
                        append("&r=${IMSI}")//网络运行商
                    }
                }

        //new request
        var request: Request = chain.request()
        val body = request.body()
        var strBody = ""

        if (body is MultipartBody) {//文件流body不处理

        } else {
            body?.let {
                val buffer = Buffer()
                it.writeTo(buffer)

                var charset: Charset? = Charset.forName("UTF-8")
                val contentType = it.contentType()
                if (contentType != null) {
                    charset = contentType.charset(charset)
                }
                if (charset != null) {
                    strBody = buffer.readString(charset)
                }
                strBody = StringUtils.parseBodyString(strBody)
            }
        }

        val httpUrl: HttpUrl = request.url().newBuilder()
                .build()
        request = request.newBuilder()
//                .addHeader("header", "$lmInfoParams")
                .url(httpUrl).build()

        var proceed: Response? = null
        try {
            proceed = chain.proceed(request)
        } catch (e: Exception) {
            //不抛出异常,上报错误
            return Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(408)
                    .message("Unsatisfiable Request ${e.message}")
                    .body(object : ResponseBody() {
                        override fun contentType(): MediaType? = null

                        override fun contentLength(): Long = 0

                        override fun source(): BufferedSource = Buffer()
                    })
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build()

        }
        return proceed!!
    }
}