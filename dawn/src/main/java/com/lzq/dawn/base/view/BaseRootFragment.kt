package com.lzq.dawn.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
 *
 * 管理视图初始化、网络监听与加载弹窗的根片段。
 */
abstract class BaseRootFragment : Fragment(), IBaseRootView, OnNetworkStatusChangedListener {

    /**
     * 占位弹窗
     */
    private var loadingDialog: ShapeLoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NetworkUtils.registerNetworkStatusChangedListener(this)

        if (javaClass.isAnnotationPresent(RegisterEventBus::class.java) && !EventBusUtils.isRegistered(this)) {
            EventBusUtils.register(this)
        }
        onViewLifecycleCreated(view, savedInstanceState)

        initView()
        initData()
        initRequest()
    }

    /** 在视图所有者建立后、页面初始化回调前执行。 */
    protected open fun onViewLifecycleCreated(view: View, savedInstanceState: Bundle?) = Unit

    override fun onDestroyView() {
        NetworkUtils.unregisterNetworkStatusChangedListener(this)
        loadingDialog?.dismiss()
        loadingDialog = null
        super.onDestroyView()
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
        if (view == null || !isAdded) return
        val dialog = loadingDialog ?: ShapeLoadingDialog.Builder(requireActivity())
            .cancelable(false).canceledOnTouchOutside(false).build().also { loadingDialog = it }
        dialog.builder.setContentTxt(content)
        dialog.show()
    }


    /**
     * 隐藏弹窗
     */
    override fun hideLoading() {
        loadingDialog?.dismiss()
    }

}
