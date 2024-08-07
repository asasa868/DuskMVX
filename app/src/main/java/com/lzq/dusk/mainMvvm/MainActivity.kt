package com.lzq.dusk.mainMvvm


import android.content.Intent
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dusk.login.LoginActivity
import com.example.main_compose.MainActivity
import com.lzq.dawn.mvvm.v.BaseMvvmActivity
import com.lzq.dusk.databinding.ActivityMainBinding
import com.lzq.dusk.mainMvi.MainMviActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {


    override fun initView() {}

    override fun initData() {}

    override fun initRequest() {}

    override fun initObserver() { obText() }

    override fun createBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun createViewModel() = ViewModelProvider(this)[MainViewModel::class.java]


    private fun obText() {
        mViewModel.testFlow()

        mViewModel.successTime.observe(this) {
            mViewBinding.tvSuccessTime.text = "success:$it"
        }
        mViewModel.successOrFailureTime.observe(this) {
            mViewBinding.tvSuccessOrFailureTime.text = "successOrFailureTime:$it"
        }
        mViewModel.successRxjavaTime.observe(this) {
            mViewBinding.tvSuccessRxjavaTime.text = "successRxjavaTime:$it"
        }
        mViewModel.successOrFailureRxjavaTime.observe(this) {
            mViewBinding.tvSuccessOrFailureRxjavaTime.text = "successOrFailureRxjavaTime:$it"
        }

        mViewBinding.btnMviActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity,MainMviActivity::class.java))
        }
        mViewBinding.btnLoginActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        }
        mViewBinding.btnComposeActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity,MainActivity::class.java))
        }
    }

}