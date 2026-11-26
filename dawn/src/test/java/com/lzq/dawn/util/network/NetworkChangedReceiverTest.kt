package com.lzq.dawn.util.network

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Looper
import com.lzq.dawn.DawnBridge
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkInfo

/**
 * @projectName com.lzq.dawn.util.network.NetworkChangedReceiverTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:37:15
 * @version 0.0.30
 * @description: 验证网络监听器的并发修改、去重、注销与通知顺序
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class, manifest = Config.NONE)
class NetworkChangedReceiverTest {
    private val registered = mutableListOf<OnNetworkStatusChangedListener>()
    private val receiver get() = NetworkChangedReceiver.instance
    private val application get() = RuntimeEnvironment.getApplication()
    private val connectivity get() = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Before
    fun initializeApplicationAndDisconnectedNetwork() {
        DawnBridge.init(application)
        shadowOf(connectivity).setActiveNetworkInfo(null)
        shadowOf(connectivity).setNetworkInfo(ConnectivityManager.TYPE_ETHERNET, null)
    }

    @After
    fun releaseListeners() {
        registered.forEach { receiver.unregisterListener(it) }
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun connectedCallbacksCanUnregisterThemselvesWithoutBreakingOtherListeners() {
        var calls = 0
        repeat(3) {
            register(object : OnNetworkStatusChangedListener {
                override fun onConnected(networkType: NetworkType?) {
                    calls++
                    receiver.unregisterListener(this)
                }
                override fun onDisconnected() = Unit
            })
        }

        connectAndDispatch()

        assertEquals(3, calls)
        assertTrue(registered.none { receiver.isRegistered(it) })
    }

    @Test
    fun ownerRemovedByAnEarlierCallbackDoesNotReceiveStaleNotification() {
        var calls = 0
        repeat(3) {
            register(object : OnNetworkStatusChangedListener {
                override fun onConnected(networkType: NetworkType?) {
                    calls++
                    registered.forEach { receiver.unregisterListener(it) }
                }
                override fun onDisconnected() = Unit
            })
        }

        connectAndDispatch()

        assertEquals(1, calls)
        assertTrue(registered.none { receiver.isRegistered(it) })
    }

    @Test
    fun duplicateRegistrationNotifiesOnceAndUnregisterReleasesTheOwner() {
        var calls = 0
        val listener = object : OnNetworkStatusChangedListener {
            override fun onConnected(networkType: NetworkType?) { calls++ }
            override fun onDisconnected() = Unit
        }
        register(listener)
        receiver.registerListener(listener)

        connectAndDispatch()
        receiver.unregisterListener(listener)

        assertEquals(1, calls)
        assertFalse(receiver.isRegistered(listener))
    }

    private fun register(listener: OnNetworkStatusChangedListener) {
        registered += listener
        receiver.registerListener(listener)
    }

    private fun connectAndDispatch() {
        shadowOf(connectivity).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, true,
        ))
        receiver.onReceive(application, Intent(ConnectivityManager.CONNECTIVITY_ACTION))
        shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.SECONDS)
    }
}

