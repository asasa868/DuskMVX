package com.lzq.dawn.base.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.lzq.dawn.base.RegisterEventBus
import com.lzq.dawn.util.EventBusUtils
import com.lzq.dawn.util.network.NetworkType
import com.lzq.dawn.util.network.NetworkUtils
import com.lzq.dawn.util.network.OnNetworkStatusChangedListener
import com.lzq.dawn.view.ShapeLoadingDialog

/**
 * @projectName com.lzq.dawn.base
 * @author Lzq
 * @description: 最初始的Activity
 * @date : Created by Lzq on 2023/12/21 10:13
 * @version 0.0.1
 */
abstract class BaseRootActivity : AppCompatActivity(), IBaseRootView, OnNetworkStatusChangedListener {

    /**
     * 占位弹窗
     */
    private val loadingDialog by lazy {
        ShapeLoadingDialog.Builder(this)
            .cancelable(false)
            .canceledOnTouchOutside(true)
            .build()
    }

    /**
     * 网络是否连接
     */
    private var isNetConnected: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayoutId() != 0) {
            setContentView(getLayoutId())
        }
        if (javaClass.isAnnotationPresent(RegisterEventBus::class.java)) {
            EventBusUtils.register(this)
        }
        ARouter.getInstance().inject(this)
        NetworkUtils.registerNetworkStatusChangedListener(this)

        initView()
        initData()
        initRequest()
    }

    override fun onDestroy() {
        if (EventBusUtils.isRegistered(this)) {
            EventBusUtils.unRegister(this)
        }
        super.onDestroy()
    }

    /**
     * 显示弹窗
     */
    override fun showLoading(content: String?) {
        loadingDialog.builder.setContentTxt(content)
        loadingDialog.show()
    }

    /**
     * 隐藏弹窗
     */
    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    /**
     * 网络已连接
     * @param networkType 网络类型
     */
    override fun onConnected(networkType: NetworkType?) {
        isNetConnected = true
    }

    /**
     * 网络已断开
     */
    override fun onDisconnected() {
        isNetConnected = false
    }

    /**
     * 网络是否连接
     */
    override fun getNetConnected(): Boolean? {
        return isNetConnected
    }


}