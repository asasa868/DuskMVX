package com.example.dusk.consumer

import androidx.viewbinding.ViewBinding
import com.google.auto.service.AutoService
import com.lzq.dawn.mvvm.v.BaseMvvmFragment
import com.lzq.dawn.mvvm.vm.IBaseMvvmViewModel
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.sseClient.SseClient
import com.lzq.dawn.sseClient.SseConfig
import com.lzq.dawn.sseClient.SseEvent
import com.lzq.dawn.util.notification.ChannelConfig
import com.lzq.dawn.util.notification.NotificationUtils
import kotlinx.coroutines.flow.Flow

/**
 * @projectName com.example.dusk.consumer.PublicApiConsumer
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:15:01
 * @version 0.0.30
 * @description: 通过候选公开坐标验证页面、网络结果、事件流与通知接口的可消费性
 *
 * 只通过候选公开坐标编译框架页面入口，避免内部工程依赖掩盖发布缺陷。
 */
abstract class PublicApiConsumer<VB : ViewBinding, VM : IBaseMvvmViewModel> : BaseMvvmFragment<VB, VM>() {
    /** 编译网络结果的原有构造及新增原始响应读取入口。 */
    fun readResult(result: DawnHttpResult<String>): String? = when (result) {
        is DawnHttpResult.Success -> result.data
        is DawnHttpResult.Failure -> result.httpResponse?.code()?.toString()
    }

    /** 只创建冷流，不发起真实网络请求。 */
    fun events(url: String): Flow<SseEvent> = SseClient(SseConfig(url)).connect()

    /** 直接引用传递注解类型，证明业务源码无需依赖处理器实现。 */
    fun annotationType(): Class<AutoService> = AutoService::class.java

    /** 保留公开重要度常量的调用方式，并由独立消费者的静态检查验证注解兼容。 */
    fun notificationChannel(id: String): ChannelConfig =
        ChannelConfig(id, id, NotificationUtils.IMPORTANCE_DEFAULT)
}

