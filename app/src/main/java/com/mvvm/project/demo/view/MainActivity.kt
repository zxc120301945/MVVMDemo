package com.mvvm.project.demo.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mvvm.project.demo.R
import com.mvvm.project.demo.common.base.BaseActivity
import com.mvvm.project.demo.common.util.ToastUtils
import com.mvvm.project.demo.model.bean.MainResult
import com.mvvm.project.demo.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip

class MainActivity : BaseActivity() {

    private lateinit var mViewModel: MainViewModel

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initViews(rootView: View?, savedInstanceState: Bundle?) {
        setSwipeRefreshStytle(srl_refresh_view, this)
        rv_list_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_list_view.adapter = mAdapter
        initViewModels()
        mViewModel.loadData(true)
    }

    override fun initEvents(rootView: View?) {
        super.initEvents(rootView)
        srl_refresh_view.setOnRefreshListener({
            mViewModel.loadData(true)
        })
        mAdapter.setOnLoadMoreListener({
            mViewModel.loadData(false)
        }, rv_list_view)
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)
            if (item != null && item is MainResult) {
                ToastUtils.show(item.abstract)
            }
        }
    }

    private fun initViewModels() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mViewModel.isShowLoading.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            isShowLoading(it)
        })

        mViewModel.refreshData.observe(this, Observer {
            it ?: return@Observer
            refreshDatas(true, it)
        })

        mViewModel.updateData.observe(this, Observer {
            it ?: return@Observer
            refreshDatas(false, it)
        })

        mViewModel.finalState.observe(this, Observer {
            finalDo()
        })
    }

    private fun refreshDatas(isPull: Boolean, list: ArrayList<MainResult>) {
        when (isPull) {
            true -> {
                mAdapter.setNewData(list)
            }
            else -> {
                mAdapter.addData(list)
            }
        }
        showLoadEnd(list)
    }

    fun finalDo() {
        srl_refresh_view?.isRefreshing = false
    }

    private fun showLoadEnd(list: ArrayList<MainResult>) {
        mAdapter ?: return
        when (list.isEmpty()) {
            true -> {
                if (mAdapter.data != null && !mAdapter.data.isEmpty()) {
                    mAdapter.loadMoreEnd()
                } else {
                    ToastUtils.show("没有数据")
                }
            }
        }
    }

    private fun isShowLoading(isShow: Boolean) {
        when (isShow) {
            true -> ToastUtils.show("展示刷新Loading")
            else -> ToastUtils.show("隐藏刷新Loading")
        }
    }

    /**
     * 设置下拉刷新的风格
     */
    fun setSwipeRefreshStytle(swipeRefreshLayout: SwipeRefreshLayout, context: Context) {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_light)
        swipeRefreshLayout.setProgressViewOffset(true, -context.dip(20), context.resources.getDimensionPixelOffset(R.dimen.progress_view_end))
    }

    private val mAdapter: BaseQuickAdapter<MainResult, BaseViewHolder> by lazy {
        object : BaseQuickAdapter<MainResult, BaseViewHolder>(R.layout.item_list_main) {
            override fun convert(holder: BaseViewHolder, item: MainResult) {
                holder.setText(R.id.tv_item, item.abstract)
            }
        }
    }
}
