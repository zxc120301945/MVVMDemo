package com.mvvm.project.demo.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.mvvm.project.demo.common.base.BaseViewModel
import com.mvvm.project.demo.common.util.NetWorkUtils
import com.mvvm.project.demo.common.util.ToastUtils
import com.mvvm.project.demo.model.api.MainService
import com.mvvm.project.demo.model.bean.MainResult
import com.mvvm.project.demo.model.dao.MainResultBase
import com.mvvm.project.demo.model.dao.MainResultDao
import com.mvvm.project.demo.model.net.Requests
import handleResponse
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
        if (!NetWorkUtils.isNetConnected()) {
            getResults({
                if(it == null){
                    refreshData.value = arrayListOf<MainResult>()
                }else{
                    refreshData.value = it
                }
                isShowLoading.value = false
                finalState.value = true
            })
        } else {
            mService.getJRTTList().handleResponse(makeSubscriber<ArrayList<MainResult>> {
                refreshData.value = it
                saveDatas(it)
            }.ifError {
                refreshData.value = null
            }.withFinalCall {
                isShowLoading.value = false
                finalState.value = true
            })
        }
    }

    private fun loadMoreDatas() {
        if (!NetWorkUtils.isNetConnected()) {
            updateData.value = arrayListOf<MainResult>()
            finalState.value = true
            return
        }
        mService.getJRTTList().handleResponse(makeSubscriber<ArrayList<MainResult>> {
            updateData.value = it
        }.ifError {
            updateData.value = null
        }.withFinalCall {
            finalState.value = true
        })
    }

    private fun saveDatas(datas: ArrayList<MainResult>) {
        Completable.fromAction({
            val database = MainResultBase.getInstance()
            database.beginTransaction()
            try {
                database.mainResultDao().delete()
                database.mainResultDao().insert(datas)
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                database.endTransaction()
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, { e ->
                    e.printStackTrace()
                })
    }

    fun getResults(callback: (ArrayList<MainResult>?) -> Unit) {
        val datas = arrayListOf<MainResult>()
        getResultsToDB().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        datas.addAll(it)
                    }
                    callback(datas)
                }, {
                    logger.info("${it.message}")
                    callback(null)
                })
    }

    private fun getResultsToDB() = MainResultBase.getInstance().mainResultDao().getResults().map { liveMsg ->
        return@map liveMsg
    }
}