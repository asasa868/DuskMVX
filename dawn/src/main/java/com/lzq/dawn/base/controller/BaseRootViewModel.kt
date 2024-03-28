package com.lzq.dawn.base.controller

import androidx.lifecycle.ViewModel
import com.lzq.dawn.base.model.IBaseRootRepository

/**
 * @projectName com.lzq.dawn
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 14:49
 * @version 0.0.1
 * @description: 最初始的ViewModel
 */
abstract class BaseRootViewModel<M : IBaseRootRepository> : ViewModel(), IBaseRootViewModel<M>
