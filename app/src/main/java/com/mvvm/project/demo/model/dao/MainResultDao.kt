package com.mvvm.project.demo.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.mvvm.project.demo.model.bean.MainResult
import io.reactivex.Single

/**
 * Created by my on 2018/08/23 0023.
 */
@Dao
interface MainResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: MainResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: List<MainResult>)

    @Query("SELECT * FROM MainResult")
    fun getResults(): Single<List<MainResult>>

    //key  //LIMIT 1表示取指定一列数据
    @Query("SELECT * FROM MainResult WHERE key = :key ")
    fun getResult(key: Int): Single<MainResult>

    @Query("DELETE FROM MainResult")
    fun delete()
}