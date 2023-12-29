package com.lzq.dawn.base.model

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @projectName com.lzq.dawn.base.model
 * @author Lzq
 * @date : Created by Lzq on 2023/12/28 17:55
 * @version
 * @description: 最初始的Model
 */
abstract class BaseRootRepository :IBaseRootRepository {



    override fun <T> requestToFlow(requestBlock: suspend FlowCollector<T>.() -> Unit): Flow<T> {
        return flow(block = requestBlock).flowOn(Dispatchers.IO)
    }


    override fun <T : Any> requestToRxjava(
        requestBlock: () -> T,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ) {
        Observable.create { emitter ->
            try {
                val result = requestBlock()
                emitter.onNext(result)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    onError(e)
                }

                override fun onComplete() {
                    onComplete()
                }

                override fun onNext(t: T) {
                    onSuccess(t)
                }

            })
    }
}