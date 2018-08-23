package com.mvvm.project.demo.common.base

import android.os.Bundle
import android.view.View
import com.mvvm.project.demo.common.util.LogUtils
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.jetbrains.anko.contentView

/**
 * 基类，可以用于处理所有页面相同的逻辑或UI
 */
abstract class BaseActivity : RxAppCompatActivity() {

    protected val logger = LogUtils.getLogger(this.javaClass.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = getLayoutId()
        if (layoutId > 0) {
            this.setContentView(layoutId)
            initViews(contentView, savedInstanceState)
            initEvents(contentView)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
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

    protected abstract fun initViews(rootView: View?, savedInstanceState: Bundle?)
    protected abstract fun getLayoutId(): Int
    protected open fun initEvents(rootView: View?) {}
}