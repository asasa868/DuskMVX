package com.lzq.dawn.mvi.view.v

import androidx.lifecycle.ViewModelStore
import com.lzq.dawn.base.model.BaseRootRepository
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.base.state.ViewStateException
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @projectName com.lzq.dawn.mvi.view.v.BaseMviViewModelTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 13:58:04
 * @version 0.0.30
 * @description: 验证意图状态转换、并发输出、加载计数与取消处理
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseMviViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun installMainDispatcher() { Dispatchers.setMain(dispatcher) }

    @After
    fun restoreMainDispatcher() { Dispatchers.resetMain() }

    @Test
    fun acknowledgedFlowDeliversToAnActiveCollector() = runTest(dispatcher) {
        val flow = AcknowledgedIntentFlow<Int>()
        val received = mutableListOf<Int>()
        val collector = backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            flow.collect { received += it }
        }

        flow.emit(42)

        assertEquals(listOf(42), received)
        collector.cancel()
    }

    @Test
    fun resultIsDeliveredOnceAndNotReplayedToLaterCollector() = runTest(dispatcher) {
        val model = Model()
        val received = mutableListOf<BaseViewState>()
        val collector = backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            model.outputIntent { received += it.mViewState }
        }
        runCurrent()

        model.flowResult(BaseMviIntent(), flowOf(1)) { BaseMviIntent().also { it.mViewState.data = 1 } }
        // 后台观察者也参与握手，必须执行当前时刻的全部任务。
        runCurrent()
        assertEquals(listOf(BaseViewState.Loading, BaseViewState.Result, BaseViewState.Success), received)
        assertFalse(model.loadingState.value)
        collector.cancel()
        received.clear()
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            model.outputIntent { received += it.mViewState }
        }
        runCurrent()
        assertTrue(received.isEmpty())
    }

    @Test
    fun failureDoesNotPublishSuccessAndStopsLoading() = runTest(dispatcher) {
        val model = Model()
        val received = mutableListOf<BaseViewState>()
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            model.outputIntent { received += it.mViewState }
        }
        runCurrent()

        model.flowResult(BaseMviIntent(), flow<Int> { throw ViewStateException("失败") }) { BaseMviIntent() }
        runCurrent()

        assertEquals(2, received.size)
        assertEquals(BaseViewState.Loading, received.first())
        assertEquals(BaseViewState.Error.VIEW_STATE, (received.last() as BaseViewState.Error).code)
        assertFalse(model.loadingState.value)
    }

    @Test
    fun loadingSurvivesCollectorAbsenceWithoutReplayingOneShotEvents() = runTest(dispatcher) {
        val model = Model()
        model.flowResult(BaseMviIntent(), flow<Int> { awaitCancellation() }) { BaseMviIntent() }
        runCurrent()
        assertTrue(model.loadingState.value)
        val received = mutableListOf<BaseViewState>()
        backgroundScope.launch {
            model.outputIntent { received += it.mViewState }
        }
        runCurrent()
        assertTrue(received.isEmpty())

        ViewModelStore().also { it.put("model", model); it.clear() }
        runCurrent()
        assertFalse(model.loadingState.value)
        assertTrue(received.isEmpty())
    }

    @Test
    fun explicitCancellationPublishesNeitherErrorNorSuccess() = runTest(dispatcher) {
        val model = Model()
        val received = mutableListOf<BaseViewState>()
        backgroundScope.launch {
            model.outputIntent { received += it.mViewState }
        }
        runCurrent()

        model.flowResult(BaseMviIntent(), flow<Int> { throw CancellationException("取消") }) { BaseMviIntent() }
        runCurrent()

        assertEquals(listOf(BaseViewState.Loading), received)
        assertFalse(model.loadingState.value)
    }

    @Test
    fun slowCollectorSeesEveryStateBeforeSameIntentIsMutatedAgain() = runTest(dispatcher) {
        val model = Model()
        val intent = BaseMviIntent()
        val received = mutableListOf<Pair<BaseViewState, Any?>>()
        backgroundScope.launch {
            model.outputIntent {
                delay(10)
                received += it.mViewState to it.mViewState.data
            }
        }
        runCurrent()

        model.flowResult(intent, flowOf(11, 22)) { value ->
            intent.also { it.mViewState.data = value }
        }
        advanceTimeBy(100)
        runCurrent()

        assertEquals(listOf(BaseViewState.Loading, BaseViewState.Result, BaseViewState.Result, BaseViewState.Success),
            received.map { it.first })
        assertEquals(listOf(11, 22), received.filter { it.first is BaseViewState.Result }.map { it.second })
        assertFalse(model.loadingState.value)
    }

    @Test
    fun twoCollectorsBothFinishBeforeNextIntentState() = runTest(dispatcher) {
        val model = Model()
        val intent = BaseMviIntent()
        val first = mutableListOf<BaseViewState>()
        val second = mutableListOf<BaseViewState>()
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) { model.outputIntent { delay(10); first += it.mViewState } }
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) { model.viewStateFlow.collect { delay(30); second += it.mViewState } }
        runCurrent()

        model.flowResult(intent, flowOf(1)) { intent }
        advanceTimeBy(200)
        runCurrent()

        val expected = listOf(BaseViewState.Loading, BaseViewState.Result, BaseViewState.Success)
        assertEquals(expected, first)
        assertEquals(expected, second)
        assertFalse(model.loadingState.value)
    }

    @Test
    fun separateModelsPreservePayloadWhileAnotherModelsCollectorIsSuspended() = runTest(dispatcher) {
        val firstModel = Model()
        val secondModel = Model()
        val firstIntent = BaseMviIntent()
        val secondIntent = BaseMviIntent()
        val firstEntered = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        val firstReceived = mutableListOf<Pair<BaseViewState, Any?>>()
        val secondReceived = mutableListOf<Pair<BaseViewState, Any?>>()
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            firstModel.outputIntent { intent ->
                if (intent.mViewState is BaseViewState.Result) {
                    firstEntered.complete(Unit)
                    releaseFirst.await()
                }
                firstReceived += intent.mViewState to intent.mViewState.data
            }
        }
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            secondModel.outputIntent { secondReceived += it.mViewState to it.mViewState.data }
        }

        firstModel.flowResult(firstIntent, flowOf(11)) { value ->
            firstIntent.also { it.mViewState.data = value }
        }
        runCurrent()
        assertTrue(firstEntered.isCompleted)
        secondModel.flowResult(secondIntent, flowOf(22)) { value ->
            secondIntent.also { it.mViewState.data = value }
        }
        // 独立实例锁会让第二个模型先覆盖共享结果；第一个观察者恢复后必须仍读取自己的载荷。
        runCurrent()
        releaseFirst.complete(Unit)
        runCurrent()

        val expectedStates = listOf(BaseViewState.Loading, BaseViewState.Result, BaseViewState.Success)
        assertEquals(expectedStates, firstReceived.map { it.first })
        assertEquals(expectedStates, secondReceived.map { it.first })
        assertEquals(listOf(11), firstReceived.filter { it.first is BaseViewState.Result }.map { it.second })
        assertEquals(listOf(22), secondReceived.filter { it.first is BaseViewState.Result }.map { it.second })
        assertFalse(firstModel.loadingState.value)
        assertFalse(secondModel.loadingState.value)
    }

    @Test
    fun cancellingCollectorReleasesPendingAcknowledgement() = runTest(dispatcher) {
        val model = Model()
        val entered = CompletableDeferred<Unit>()
        val collector = backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            model.outputIntent { entered.complete(Unit); awaitCancellation() }
        }
        runCurrent()
        model.flowResult(BaseMviIntent(), flowOf(1)) { BaseMviIntent() }
        runCurrent()
        assertTrue(entered.isCompleted)
        assertTrue(model.loadingState.value)

        collector.cancel()
        runCurrent()

        assertFalse(model.loadingState.value)
    }

    @Test
    fun throwingCollectorDoesNotHangPublisherOrOtherCollectors() = runTest(dispatcher) {
        val model = Model()
        val received = mutableListOf<BaseViewState>()
        var failed = false
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                model.outputIntent { throw IllegalStateException("观察者失败") }
            } catch (_: IllegalStateException) { failed = true }
        }
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) { model.outputIntent { received += it.mViewState } }
        runCurrent()

        model.flowResult(BaseMviIntent(), flowOf(1)) { BaseMviIntent() }
        runCurrent()

        assertTrue(failed)
        assertEquals(listOf(BaseViewState.Loading, BaseViewState.Result, BaseViewState.Success), received)
        assertFalse(model.loadingState.value)
    }

    @Test
    fun clearingViewModelCancelsPublisherWaitingForCollector() = runTest(dispatcher) {
        val model = Model()
        val store = ViewModelStore().also { it.put("model", model) }
        val received = mutableListOf<BaseViewState>()
        backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
            model.outputIntent { received += it.mViewState; awaitCancellation() }
        }
        runCurrent()
        model.flowResult(BaseMviIntent(), flowOf(1)) { BaseMviIntent() }
        runCurrent()
        assertTrue(model.loadingState.value)

        store.clear()
        runCurrent()

        assertFalse(model.loadingState.value)
        assertEquals(listOf(BaseViewState.Loading), received)
    }

    /**
     * @projectName com.lzq.dawn.mvi.view.v.BaseMviViewModelTest.Repository
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:58:04
     * @version 0.0.30
     * @description: 为意图状态模型测试提供仓库类型
     */
    class Repository : BaseRootRepository()
    /**
     * @projectName com.lzq.dawn.mvi.view.v.BaseMviViewModelTest.Model
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:58:04
     * @version 0.0.30
     * @description: 为意图状态模型测试创建仓库并暴露待测模型
     */
    class Model : BaseMviViewModel<BaseMviIntent, Repository>() {
        override fun handleIntent(intent: BaseMviIntent) = Unit
        override fun createModel() = Repository()
    }
}

