package com.lzq.dusk.mainMvi

import androidx.lifecycle.lifecycleScope
import com.hjq.toast.Toaster
import com.lzq.dawn.mvi.v.BaseMviActivity
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.tools.extensions.viewModel
import com.lzq.dusk.databinding.ActivityMainMviBinding
import com.lzq.dusk.network.MainService
import kotlinx.coroutines.launch

class MainMviActivity : BaseMviActivity<MainMviViewModel, ActivityMainMviBinding, MainMviIntent>() {
    override fun initView() {

        lifecycleScope.launch {
            when (val banner = MainService.getApiService().getBanner()) {
                is DawnHttpResult.Success -> {
                    Toaster.show(banner.data.toString())
                }

                is DawnHttpResult.Failure -> {
                    Toaster.show("${banner.code}:${banner.message}")
                }
            }
        }
    }

    override fun initData() {

    }

    override fun initRequest() {

    }

    override fun initObserver() {

    }

    override fun outputViewIntent(intent: MainMviIntent) {
        Toaster.show(intent.toString())
    }

    override fun createBinding(): ActivityMainMviBinding = ActivityMainMviBinding.inflate(layoutInflater)
    override fun createViewModel(): MainMviViewModel = viewModel(MainMviViewModel::class.java)


}