package com.mvvm.project.demo.model.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**
 * Created by my on 2018/08/21 0021.
 */
@Entity
data class MainResult(
        @PrimaryKey(autoGenerate = true)
        var key: Int = 0,
        var title: String = "",
        var data: String = ""
) : Serializable
