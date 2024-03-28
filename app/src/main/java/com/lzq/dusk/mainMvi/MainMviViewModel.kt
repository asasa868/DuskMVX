package com.lzq.dusk.mainMvi

import androidx.lifecycle.viewModelScope
import com.hjq.toast.Toaster
import com.lzq.dawn.mvi.view.v.BaseMviViewModel
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dusk.network.bean.BannerBean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @projectName com.lzq.dusk
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 11:51
 * @version
 * @description:
 */
class MainMviViewModel : BaseMviViewModel<MainMviIntent, MainMviRepository>() {
    private var _bannerList: MutableSharedFlow<List<BannerBean>> = MutableSharedFlow()
    val bannerList get() = _bannerList

    override fun handleViewState(intent: MainMviIntent) {
        when (intent) {
            is MainMviIntent.BannerIntent -> {
                getBanner(intent)
            }
        }
    }

    override fun createModel(): MainMviRepository {
        return MainMviRepository()
    }

    private fun getBanner(intent: MainMviIntent.BannerIntent) {
        val request = mModel.requestToFlow { emit(mModel.getBanner()) }

        flowResult(
            intent,
            request,
        ) {
            when (it) {
                is DawnHttpResult.Success -> {
                    _bannerList.emit(it.data)
                    intent
                }

                is DawnHttpResult.Failure -> {
                    Toaster.show(it.message)
                    intent
                }
            }
        }
    }

}
