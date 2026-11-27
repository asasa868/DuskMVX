package com.lzq.dawn.util.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Looper
import com.lzq.dawn.DawnBridge
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.Shadows.shadowOf

/**
 * @projectName com.lzq.dawn.util.network.NetworkChangedReceiverTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:37:15
 * @version 0.0.30
 * @description: 验证网络监听器的并发修改、去重、注销与通知顺序
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 28, 34, 37], application = Application::class, manifest = Config.NONE)
class NetworkChangedReceiverTest {
    private val registered = mutableListOf<OnNetworkStatusChangedListener>()
    private val receiver get() = NetworkChangedReceiver.instance
    private val application get() = RuntimeEnvironment.getApplication()

    @Before
    fun initializeApplicationAndDisconnectedNetwork() {
        DawnBridge.init(application)
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

        receiver.dispatchNetworkTypeChanged(NetworkType.NETWORK_WIFI)

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

        receiver.dispatchNetworkTypeChanged(NetworkType.NETWORK_WIFI)

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

        receiver.dispatchNetworkTypeChanged(NetworkType.NETWORK_WIFI)
        receiver.unregisterListener(listener)

        assertEquals(1, calls)
        assertFalse(receiver.isRegistered(listener))
    }

    @Test
    fun nullOwnersDoNotRegisterASystemCallback() {
        val manager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callbacksBefore = shadowOf(manager).networkCallbacks.size

        receiver.registerListener(null)
        receiver.unregisterListener(null)

        assertEquals(callbacksBefore, shadowOf(manager).networkCallbacks.size)
        assertFalse(receiver.isRegistered(null))
    }

    @Test
    fun lastOwnerReleasesTheOnlySystemCallback() {
        val manager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val listener = object : OnNetworkStatusChangedListener {
            override fun onConnected(networkType: NetworkType?) = Unit
            override fun onDisconnected() = Unit
        }
        register(listener)
        receiver.registerListener(listener)

        assertEquals(1, shadowOf(manager).networkCallbacks.size)
        receiver.unregisterListener(listener)

        assertEquals(0, shadowOf(manager).networkCallbacks.size)
    }

    private fun register(listener: OnNetworkStatusChangedListener) {
        registered += listener
        receiver.registerListener(listener)
    }

}

