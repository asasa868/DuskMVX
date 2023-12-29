package com.lzq.dusk


import com.lzq.dawn.mvvm.vm.BaseMvvmViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * @Name   : MainViewModel
 * @Time   : 2022/12/21  9:32
 * @Author :  Lzq
 * @Desc   :
 */
class MainViewModel : BaseMvvmViewModel<MainRepository>() {


    val timeFlow = flow {
        var time = 0
        while (true) {
            emit(time)
            delay(1000)
            time++
        }
    }

    override fun createModel(): MainRepository {
        return MainRepository()
    }


}