package com.mvvm.project.demo.common.util

/**
 * Created by my on 2018/08/21 0021.
 */
object CommonUtils {
    //stetho是否准备好
    fun isStethoPresent(): Boolean {
        try {
            Class.forName("com.facebook.stetho.Stetho")
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }

    }
}