package com.mvvm.project.demo.common.util

import android.util.Log
import com.mvvm.project.demo.BuildConfig
import java.util.logging.Level
import java.util.logging.Logger

/**
 * 日志工具类
 */
class LogUtils private constructor() {


    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        fun getLogger(tag: String): Logger {
            val logger = Logger.getLogger(tag)
            logger.level = if (BuildConfig.DEBUG) Level.ALL else Level.OFF
            return logger
        }

        var isDebug = BuildConfig.DEBUG//
        private val TAG = "log"

        // 下面四个是默认tag的函数
        fun i(msg: String) {
            if (isDebug)
                Log.i(TAG, msg)
        }

        fun d(msg: String) {
            if (isDebug)
                Log.d(TAG, msg)
        }

        fun e(msg: String) {
            if (isDebug)
                Log.e(TAG, msg)
        }

        fun v(msg: String) {
            if (isDebug)
                Log.v(TAG, msg)
        }

        // 下面是传入自定义tag的函数
        fun i(tag: String, msg: String) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (isDebug)
                Log.d(tag, msg)
        }

        fun e(tag: String, msg: String) {
            if (isDebug)
                Log.e(tag, msg)
        }

        fun v(tag: String, msg: String) {
            if (isDebug)
                Log.v(tag, msg)
        }
    }

}