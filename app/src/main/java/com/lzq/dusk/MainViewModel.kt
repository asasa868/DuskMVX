package com.lzq.dusk


import androidx.lifecycle.MutableLiveData
import com.hjq.toast.Toaster
import com.lzq.dawn.mvvm.vm.BaseMvvmViewModel
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * @Name   : MainViewModel
 * @Time   : 2022/12/21  9:32
 * @Author :  Lzq
 * @Desc   :
 */
class MainViewModel : BaseMvvmViewModel() {


    private val timeFlow = flow {
        var time = 0
        while (time < 5) {
            emit(time)
            delay(1000)
            time++
        }
    }

    private val timeRxjava = Observable.create { emitter ->
        var time = 0
        while (time < 5) {
            emitter.onNext(time)
            Thread.sleep(1000)
            time++
        }
        emitter.onComplete()
    }

    var successTime = MutableLiveData(0)
    var successOrFailureTime = MutableLiveData(0)
    var successRxjavaTime = MutableLiveData(0)
    var successOrFailureRxjavaTime = MutableLiveData(0)

    override fun createModel(): MainRepository {
        return MainRepository()
    }

    fun testFlow() {

        launchCallBackFlow(timeFlow) {
            successTime.value = it
        }


        launchCallBackFlow(timeFlow, {
            successOrFailureTime.value = it
        }, {
            Toaster.show("successOrFailureTime-----${it.localizedMessage}")
        })

        launchCallBackRxjava(timeRxjava) {
            successRxjavaTime.value = it
        }

        launchCallBackRxjava(timeRxjava, {
            successOrFailureRxjavaTime.value = it
        }) {
            Toaster.show("successOrFailureRxjavaTime-----${it.localizedMessage}")
        }
    }

}