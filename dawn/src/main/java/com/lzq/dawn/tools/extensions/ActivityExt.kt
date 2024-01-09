package com.lzq.dawn.tools.extensions

import androidx.appcompat.app.AppCompatActivity
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
 */
fun interface LifecycleStateCallBack {
    suspend fun onLifeCycle(scope: CoroutineScope)
}

fun AppCompatActivity.repeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED, lifecycleStateCallBack: LifecycleStateCallBack
) {
    lifecycleScope.launch { repeatOnLifecycle(state) { lifecycleStateCallBack.onLifeCycle(this) } }
}


inline fun <reified T : ViewModel> AppCompatActivity.viewModel(modelClass: Class<T>): T {
    return ViewModelProvider(this)[modelClass]
}
