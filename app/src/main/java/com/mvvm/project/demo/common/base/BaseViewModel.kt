package com.mvvm.project.demo.common.base

import android.arch.lifecycle.ViewModel
import com.mvvm.project.demo.common.util.LogUtils
import com.mvvm.project.demo.common.util.StringUtils
import com.mvvm.project.demo.model.net.RequestCaller

/**
 * Created by my on 2018/08/21 0021.
 */
abstract class BaseViewModel : RequestCaller, ViewModel() {
    protected val logger = LogUtils.getLogger(this.javaClass.name)
    val UUID: String by lazy { StringUtils.uuid() }


    override fun getRequestCallerId(): String {
        return this.UUID
    }

    fun onCreate() {}
}