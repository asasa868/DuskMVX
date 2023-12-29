package com.lzq.dusk


import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.lzq.dawn.mvvm.v.BaseMvvmActivity
import com.lzq.dusk.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {


    override fun initView() {

        lifecycleScope.launch {
            obText()
        }
    }

    override fun initData() {


    }

    override fun initRequest() {


    }

    override fun createBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun createViewModel() = ViewModelProvider(this)[MainViewModel::class.java]


    private suspend fun obText() {

        mViewModel.timeFlow.collect {
            mViewBinding.tvCenter.text = "-----------$it-----------"


        }

    }
}