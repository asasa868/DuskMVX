package com.lzq.dusk.mainMvvm


import android.content.Intent
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dusk.login.LoginActivity
import com.example.main_compose.MainActivity
import com.lzq.dusk.R
import com.lzq.dawn.mvvm.v.BaseMvvmActivity
import com.lzq.dawn.util.view.SystemBarInsets
import com.lzq.dusk.databinding.ActivityMainBinding
import com.lzq.dusk.mainMvi.MainMviActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {


    override fun initView() {
        SystemBarInsets.applyTo(mViewBinding.root)
    }

    override fun initData() {}

    override fun initRequest() {}

    override fun initObserver() { obText() }

    override fun createBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun createViewModel() = ViewModelProvider(this)[MainViewModel::class.java]


    private fun obText() {
        mViewModel.testFlow()

        mViewModel.successTime.observe(this) {
            mViewBinding.tvSuccessTime.text = getString(R.string.main_success_time, it)
        }
        mViewModel.successOrFailureTime.observe(this) {
            mViewBinding.tvSuccessOrFailureTime.text =
                getString(R.string.main_success_or_failure_time, it)
        }
        mViewModel.successRxjavaTime.observe(this) {
            mViewBinding.tvSuccessRxjavaTime.text = getString(R.string.main_success_rxjava_time, it)
        }
        mViewModel.successOrFailureRxjavaTime.observe(this) {
            mViewBinding.tvSuccessOrFailureRxjavaTime.text =
                getString(R.string.main_success_or_failure_rxjava_time, it)
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
