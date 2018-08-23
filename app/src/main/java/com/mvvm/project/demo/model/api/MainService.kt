package com.mvvm.project.demo.model.api

import com.mvvm.project.demo.model.bean.MainResult
import com.mvvm.project.demo.model.net.response.Root
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by my on 2018/08/21 0021.
 */
interface MainService {

    @GET("api/2/article/v23/stream/")
    fun getJRTTList(): Observable<Root<ArrayList<MainResult>>>
}