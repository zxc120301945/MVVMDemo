package com.mvvm.project.demo.common.base

import android.content.Context
import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxFragment

/**
 * 基类，可以用于处理所有页面相同的逻辑或UI
 */
abstract class BaseFragment:RxFragment(){

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }
}