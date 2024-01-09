package com.lzq.dawn.mvvm.vm

import androidx.lifecycle.MutableLiveData
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.mvvm.IViewModelResultCallBack
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

/**
 * @projectName com.lzq.dawn.mvvm.vm
 * @author Lzq
 * @date : Created by Lzq on 2024/1/3 15:42
 * @version 0.0.1
 * @description: MVVM架构模式的ViewModel接口
 */
interface IBaseMvvmViewModel {

    fun getViewState(): MutableLiveData<BaseViewState>

    fun <T> launchCallBackFlow(
        flow: Flow<T>,
        success: IViewModelResultCallBack.OnSuccess<T>,
        failure: IViewModelResultCallBack.OnFailure<Throwable>
    )

    fun <T> launchCallBackFlow(
        flow: Flow<T>,
        success: IViewModelResultCallBack.OnSuccess<T>
    )

    fun <T : Any> launchCallBackRxjava(
        observable: Observable<T>,
        success: IViewModelResultCallBack.OnSuccess<T>,
        failure: IViewModelResultCallBack.OnFailure<Throwable>
    )

    fun <T : Any> launchCallBackRxjava(
        observable: Observable<T>, success: IViewModelResultCallBack.OnSuccess<T>
    )

}