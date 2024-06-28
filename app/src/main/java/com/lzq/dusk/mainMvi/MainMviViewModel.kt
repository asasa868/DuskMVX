package com.lzq.dusk.mainMvi

import com.hjq.toast.Toaster
import com.lzq.dawn.mvi.view.v.BaseMviViewModel
import com.lzq.dawn.network.bean.DawnHttpResult

/**
 * @projectName com.lzq.dusk
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 11:51
 * @version
 * @description:
 */
class MainMviViewModel : BaseMviViewModel<MainMviIntent, MainMviRepository>() {

    override fun handleIntent(intent: MainMviIntent) {
        when (intent) {
            is MainMviIntent.BannerIntent -> getBanner(intent)
            is MainMviIntent.HarmonyIntent -> getHarmony(intent)
        }
    }

    override fun createModel(): MainMviRepository { return MainMviRepository() }

    private fun getBanner(intent: MainMviIntent.BannerIntent) {
        flowResult(intent, mModel.getBanner()) {
            when (it) {
                is DawnHttpResult.Success -> {
                    intent.mViewState.data = it.data
                }

                is DawnHttpResult.Failure -> {
                    Toaster.show(it.message)
                }
            }
            intent
        }
    }

    private fun getHarmony(intent: MainMviIntent.HarmonyIntent) {
        flowResult(intent, mModel.getHarmony()) {
            when (it) {
                is DawnHttpResult.Success -> {
                    intent.mViewState.data = it.data
                }

                is DawnHttpResult.Failure ->{
                    Toaster.show(it.message)
                }
            }
            intent
        }
    }

}
