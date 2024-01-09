package com.lzq.dawn.mvvm.vm


import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lzq.dawn.base.controller.BaseRootViewModel
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.mvvm.IViewModelResultCallBack
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @projectName com.lzq.dawn.mvvm.vm
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 14:29
 * @version 0.0.1
 * @description: MVVM架构模式的ViewModel基类
 */
abstract class BaseMvvmViewModel : BaseRootViewModel(), IBaseMvvmViewModel {

    protected val mModel by lazy { createModel() }

    private var _baseViewState = MutableLiveData<BaseViewState>()

    override fun getViewState(): MutableLiveData<BaseViewState> {
        return _baseViewState
    }

    override fun <T> launchCallBackFlow(
        flow: Flow<T>,
        success: IViewModelResultCallBack.OnSuccess<T>
    ) {
        viewModelScope.launch {
            try {
                flow
                    .onStart { _baseViewState.value = BaseViewState.Loading }
                    .onCompletion { cause -> if (cause == null) _baseViewState.value = BaseViewState.Success }
                    .catch { _baseViewState.value = BaseViewState.Error(msg = it.localizedMessage) }
                    .flowOn(AndroidUiDispatcher.Main)
                    .collect { success.onSuccess(it) }
            } catch (e: Exception) {
                _baseViewState.value = BaseViewState.Error(msg = e.localizedMessage)
            }
        }
    }

    override fun <T> launchCallBackFlow(
        flow: Flow<T>,
        success: IViewModelResultCallBack.OnSuccess<T>,
        failure: IViewModelResultCallBack.OnFailure<Throwable>
    ) {
        viewModelScope.launch {
            try {
                flow
                    .onStart { _baseViewState.value = BaseViewState.Loading }
                    .onCompletion { cause -> if (cause == null) _baseViewState.value = BaseViewState.Success }
                    .catch {
                        _baseViewState.value = BaseViewState.Error(msg = it.localizedMessage)
                        failure.onFailure(it)
                    }
                    .flowOn(AndroidUiDispatcher.Main)
                    .collect { success.onSuccess(it) }
            } catch (e: Exception) {
                _baseViewState.value = BaseViewState.Error(msg = e.localizedMessage)
                failure.onFailure(e)
            }
        }
    }

    override fun <T : Any> launchCallBackRxjava(
        observable: Observable<T>,
        success: IViewModelResultCallBack.OnSuccess<T>
    ) {
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {
                    _baseViewState.value = BaseViewState.Loading
                }

                override fun onError(e: Throwable) {
                    _baseViewState.value = BaseViewState.Error(msg = e.localizedMessage)
                }

                override fun onComplete() {
                    _baseViewState.value = BaseViewState.Success
                }

                override fun onNext(t: T) {
                    success.onSuccess(t)
                }

            })
    }

    override fun <T : Any> launchCallBackRxjava(
        observable: Observable<T>,
        success: IViewModelResultCallBack.OnSuccess<T>,
        failure: IViewModelResultCallBack.OnFailure<Throwable>
    ) {
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {
                    _baseViewState.value = BaseViewState.Loading
                }

                override fun onError(e: Throwable) {
                    _baseViewState.value = BaseViewState.Error(msg = e.localizedMessage)
                    failure.onFailure(e)
                }

                override fun onComplete() {
                    _baseViewState.value = BaseViewState.Success
                }

                override fun onNext(t: T) {
                    success.onSuccess(t)
                }

            })
    }

}