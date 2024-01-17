package com.lzq.dawn.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.lzq.dawn.base.RegisterEventBus
import com.lzq.dawn.tools.EventBusUtils
import com.lzq.dawn.util.network.NetworkUtils
import com.lzq.dawn.util.network.OnNetworkStatusChangedListener
import com.lzq.dawn.view.ShapeLoadingDialog

/**
 * @projectName com.lzq.dawn.base.view
 * @author Lzq
 * @date : Created by Lzq on 2023/12/25 17:37
 * @version 0.0.1
 * @description: 最初始的Fragment
 */
abstract class BaseRootFragment : Fragment(), IBaseRootView, OnNetworkStatusChangedListener {

    /**
     * 占位弹窗
     */
    private val loadingDialog by lazy {
        ShapeLoadingDialog.Builder(requireActivity()).cancelable(false).canceledOnTouchOutside(false).loadText("加载中...")
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ARouter.getInstance().inject(this)
        NetworkUtils.registerNetworkStatusChangedListener(this)

        if (javaClass.isAnnotationPresent(RegisterEventBus::class.java)) {
            EventBusUtils.register(this)
        }

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
        loadingDialog.hide()
    }

}