package com.lzq.dusk.mainMvi

import com.lzq.dawn.mvi.view.i.BaseMviIntent

/**
 * @projectName com.lzq.dusk.mainMvi
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 14:17
 * @version
 * @description:
 */
open class MainMviIntent : BaseMviIntent() {
    data class LoginViewState(val userName: String, val password: String) : MainMviIntent()

    class BannerIntent : MainMviIntent()
}
