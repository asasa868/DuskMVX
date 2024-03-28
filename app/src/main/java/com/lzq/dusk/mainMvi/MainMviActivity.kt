package com.lzq.dusk.mainMvi

import androidx.lifecycle.Lifecycle
import com.hjq.toast.Toaster
import com.lzq.dawn.mvi.view.v.BaseMviActivity
import com.lzq.dawn.tools.extensions.repeatOnLifecycle
import com.lzq.dawn.tools.extensions.viewModel
import com.lzq.dusk.databinding.ActivityMainMviBinding

class MainMviActivity :
    BaseMviActivity<MainMviRepository, MainMviViewModel, ActivityMainMviBinding, MainMviIntent>() {
    override fun initView() {
//        lifecycleScope.launch {
//            when (val banner = MainService.getApiService().getBanner()) {
//                is DawnHttpResult.Success -> {
//                    Toaster.show(banner.data.toString())
//                }
//
//                is DawnHttpResult.Failure -> {
//                    Toaster.show("${banner.code}:${banner.message}")
//                }
//            }
//        }
        mViewModel.handleViewState(MainMviIntent.BannerIntent())
    }

    override fun initData() {
    }

    override fun initRequest() {
    }

    override fun initObserver() {
        repeatOnLifecycle(state = Lifecycle.State.CREATED) {
            mViewModel.bannerList.collect {
                Toaster.show(it.toString())
            }
        }
    }

    override fun outputViewIntent(intent: MainMviIntent) {
        when (intent) {
            is MainMviIntent.BannerIntent -> {
                //    Toaster.show(intent)
            }
        }
    }

    override fun createBinding(): ActivityMainMviBinding = ActivityMainMviBinding.inflate(layoutInflater)

    override fun createViewModel(): MainMviViewModel = viewModel(MainMviViewModel::class.java)
}
