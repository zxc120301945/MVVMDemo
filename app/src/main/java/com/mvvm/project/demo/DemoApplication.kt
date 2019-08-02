package com.mvvm.project.demo

import android.app.Application
import com.mvvm.project.demo.common.util.AppInitUtils
import java.util.logging.Logger

/**
 * Created by my on 2018/08/21 0021.
 */
class DemoApplication : Application() {
    private val logger = Logger.getLogger("App")

    companion object {
        private lateinit var application: Application

        fun getApp(): Application {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        DemoApplication.application = this
        AppInitUtils.onCreateInit(this)
    }
}