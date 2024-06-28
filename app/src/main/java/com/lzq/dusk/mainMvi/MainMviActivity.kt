package com.lzq.dusk.mainMvi


import com.lzq.dawn.base.state.doOnResult
import com.lzq.dawn.mvi.view.v.BaseMviActivity
import com.lzq.dawn.tools.extensions.viewModel
import com.lzq.dusk.databinding.ActivityMainMviBinding

class MainMviActivity :
    BaseMviActivity<MainMviRepository, MainMviViewModel, ActivityMainMviBinding, MainMviIntent>() {
    override fun initView() {
        mViewModel.inputIntent(MainMviIntent.BannerIntent())
        mViewModel.inputIntent(MainMviIntent.HarmonyIntent())
    }

    override fun initData() {
    }

    override fun initRequest() {
    }

    override fun initObserver() {
    }

    override fun outputIntentModel(intent: MainMviIntent) {
        when (intent) {
            is MainMviIntent.BannerIntent -> {
                intent.mViewState.doOnResult {
                    mViewBinding.tvContent.text =
                        "${mViewBinding.tvContent.text}${intent.mViewState.data.toString()}"
                }
            }

            is MainMviIntent.HarmonyIntent -> {
                intent.mViewState.doOnResult {
                    mViewBinding.tvContent.text =
                        "${mViewBinding.tvContent.text}  ${intent.mViewState.data.toString()}"
                }
            }
        }
    }

    override fun createBinding(): ActivityMainMviBinding = ActivityMainMviBinding.inflate(layoutInflater)

    override fun createViewModel(): MainMviViewModel = viewModel(MainMviViewModel::class.java)
}
