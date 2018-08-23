package com.mvvm.project.demo.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.mvvm.project.demo.common.base.BaseViewModel
import com.mvvm.project.demo.model.api.MainService
import com.mvvm.project.demo.model.bean.MainResult
import com.mvvm.project.demo.model.net.Requests
import handleResponse

/**
 * Created by my on 2018/08/21 0021.
 */
class MainViewModel : BaseViewModel() {

    val isShowLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val refreshData: MutableLiveData<ArrayList<MainResult>> by lazy { MutableLiveData<ArrayList<MainResult>>() }
    val updateData: MutableLiveData<ArrayList<MainResult>> by lazy { MutableLiveData<ArrayList<MainResult>>() }
    val finalState: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    private var mIsFirst = true
    private val mService: MainService by lazy { Requests.create(MainService::class.java) }

    fun loadData(isPull: Boolean) {
        when (isPull) {
            true -> refreshDatas()
            else -> loadMoreDatas()
        }
    }

    private fun refreshDatas() {
        if (mIsFirst) {
            isShowLoading.value = true
            mIsFirst = false
        }
        mService.getJRTTList().handleResponse(makeSubscriber<ArrayList<MainResult>> {
            refreshData.value = it
        }.ifError {
            refreshData.value = null
        }.withFinalCall {
            isShowLoading.value = false
            finalState.value = true
        })
    }

    private fun loadMoreDatas() {
        mService.getJRTTList().handleResponse(makeSubscriber<ArrayList<MainResult>> {
            refreshData.value = it
        }.ifError {
            refreshData.value = null
        }.withFinalCall {
            finalState.value = true
        })
    }
}