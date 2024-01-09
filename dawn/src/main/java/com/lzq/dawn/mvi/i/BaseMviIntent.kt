package com.lzq.dawn.mvi.i

import com.lzq.dawn.base.state.BaseViewState

/**
 * @projectName com.lzq.dawn.mvi.i
 * @author Lzq
 * @date : Created by Lzq on 2024/1/4 17:43
 * @version 0.0.1
 * @description: MVI架构模式Intent的基类
 */
open class BaseMviIntent(var mViewState: BaseViewState = BaseViewState.Default)