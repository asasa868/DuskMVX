package com.lzq.dawn.tools.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @projectName com.lzq.dawn.tools.extensions
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 14:24
 * @version 0.0.1
 * @description: Activity的拓展
 *
 * 生命周期达到目标状态时执行的挂起回调。
 */
fun interface LifecycleStateCallBack {
    /** 在当前激活的协程作用域内执行。 */
    suspend fun onLifeCycle(scope: CoroutineScope)
}

/** 在活动达到目标状态时启动回调，降至目标状态以下时取消。 */
fun AppCompatActivity.repeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    lifecycleStateCallBack: LifecycleStateCallBack,
) {
    lifecycleScope.launch { repeatOnLifecycle(state) { lifecycleStateCallBack.onLifeCycle(this) } }
}

/** 在当前视图达到目标状态时执行；必须在视图创建后调用。 */
fun Fragment.repeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    lifecycleStateCallBack: LifecycleStateCallBack,
) {
    val owner = viewLifecycleOwner
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(state) { lifecycleStateCallBack.onLifeCycle(this) }
    }
}

/** 返回归当前活动所有的控制器实例。 */
inline fun <reified T : ViewModel> AppCompatActivity.viewModel(modelClass: Class<T>): T {
    return ViewModelProvider(this)[modelClass]
}
