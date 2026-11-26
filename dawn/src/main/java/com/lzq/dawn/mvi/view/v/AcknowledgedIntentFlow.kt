package com.lzq.dawn.mvi.view.v

import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow

/**
 * @projectName com.lzq.dawn.mvi.view.v.AcknowledgedIntentFlow
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:34:44
 * @version 0.0.30
 * @description: 在活动订阅者完成消费后确认意图投递以保护可变状态载荷
 *
 * 在活动观察者完成消费后才允许发送者继续修改可变意图；无观察者时不保留事件。
 */
internal class AcknowledgedIntentFlow<T> : SharedFlow<T> {
    /** 保存一次意图投递的载荷与消费完成信号。 */
    private class Emission<T>(val value: T) {
        val consumed = CompletableDeferred<Unit>()
    }

    /** 为单个观察者维护可确认消费的投递通道。 */
    private class Subscription<T> {
        val channel = Channel<Emission<T>>(
            capacity = Channel.RENDEZVOUS,
            onUndeliveredElement = { it.consumed.complete(Unit) },
        )
    }

    private val subscriptions = CopyOnWriteArrayList<Subscription<T>>()

    override val replayCache: List<T> get() = emptyList()

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        val subscription = Subscription<T>()
        subscriptions.add(subscription)
        try {
            for (emission in subscription.channel) {
                try {
                    collector.emit(emission.value)
                } finally {
                    emission.consumed.complete(Unit)
                }
            }
        } finally {
            subscriptions.remove(subscription)
            subscription.channel.cancel()
        }
        awaitCancellation()
    }

    suspend fun emit(value: T) {
        currentCoroutineContext().ensureActive()
        val pending = subscriptions.map { it to Emission(value) }
        for ((subscription, emission) in pending) {
            try {
                subscription.channel.send(emission)
            } catch (_: ClosedSendChannelException) {
                currentCoroutineContext().ensureActive()
                emission.consumed.complete(Unit)
            } catch (cancelled: CancellationException) {
                // 接收者退出仅释放本次投递，发送者自身取消仍必须继续传播。
                currentCoroutineContext().ensureActive()
                emission.consumed.complete(Unit)
            }
        }
        pending.forEach { (_, emission) -> emission.consumed.await() }
        currentCoroutineContext().ensureActive()
    }
}

