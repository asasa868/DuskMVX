package com.lzq.dusk.mainMvi

import com.hjq.toast.Toaster
import com.lzq.dawn.mvi.v.BaseMviActivity
import com.lzq.dawn.tools.extensions.viewModel
import com.lzq.dusk.databinding.ActivityMainMviBinding

class MainMviActivity : BaseMviActivity<MainMviViewModel, ActivityMainMviBinding, MainMviIntent>() {
    override fun initView() {

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