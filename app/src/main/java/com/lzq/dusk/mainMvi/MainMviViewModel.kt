package com.lzq.dusk.mainMvi

import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.mvi.i.BaseMviViewModel

/**
 * @projectName com.lzq.dusk
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 11:51
 * @version
 * @description:
 */
class MainMviViewModel : BaseMviViewModel<MainMviIntent>() {


    override fun handleViewState(intent: MainMviIntent) {

    }

    override fun createModel(): IBaseRootRepository { return MainMviRepository() }
}