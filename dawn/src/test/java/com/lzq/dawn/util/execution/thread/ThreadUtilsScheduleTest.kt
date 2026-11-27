package com.lzq.dawn.util.execution.thread

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ThreadUtilsScheduleTest {
    private val tasks = mutableListOf<DispatchTask>()
    private val executors = mutableListOf<ControlledExecutor>()

    @After
    fun releaseScheduledTasksAndExecutors() {
        tasks.forEach { it.cancel(false) }
        executors.forEach { it.shutdownNow() }
    }

    @Test fun fixedRateKeepsTheOriginalCadenceWhenTasksFinishOnTime() {
        assertEquals(120L, ThreadUtils.nextFixedRateExecution(100L, 109L, 20L))
    }

    @Test fun fixedRateSkipsMissedRunsAfterProcessSuspension() {
        assertEquals(1_120L, ThreadUtils.nextFixedRateExecution(100L, 1_100L, 20L))
    }

    @Test fun fixedRateRejectsAnIntervalThatCannotBeScheduled() {
        assertThrows(IllegalArgumentException::class.java) {
            ThreadUtils.nextFixedRateExecution(100L, 100L, 0L)
        }
    }

    @Test fun publicFixedRateEntryDispatchesItsFirstRun() {
        val executor = executor()
        val task = task { it.cancel(false) }

        ThreadUtils.executeByCustomAtFixRate(executor, task, 20L, TimeUnit.MILLISECONDS)

        assertTrue(task.firstRun.await(5L, TimeUnit.SECONDS))
        assertEquals(1, executor.dispatches.get())
        assertFalse(ThreadUtils.TASK_POOL_MAP.containsKey(task))
    }

    @Test fun invalidPeriodsAndDelaysAreRejectedBeforeTaskRegistration() {
        val executor = executor()
        val inputs = listOf(
            Triple(0L, 0L, TimeUnit.MILLISECONDS),
            Triple(0L, -1L, TimeUnit.MILLISECONDS),
            Triple(0L, 999_999L, TimeUnit.NANOSECONDS),
            Triple(-1L, 20L, TimeUnit.MILLISECONDS),
            Triple(-1L, 1_000_000L, TimeUnit.NANOSECONDS),
        )

        for ((delay, period, unit) in inputs) {
            val task = task()

            assertThrows(IllegalArgumentException::class.java) {
                ThreadUtils.executeByCustomAtFixRate(executor, task, delay, period, unit)
            }
            assertFalse(ThreadUtils.TASK_POOL_MAP.containsKey(task))
        }
        assertEquals(0, executor.dispatches.get())
    }

    @Test fun cancelingDuringDispatchStopsFutureDispatches() {
        val executor = executor(blockFirstDispatch = true)
        val task = task()
        ThreadUtils.executeByCustomAtFixRate(executor, task, 20L, TimeUnit.MILLISECONDS)
        assertTrue(executor.firstDispatch.await(5L, TimeUnit.SECONDS))

        task.cancel(false)
        executor.releaseFirstDispatch.countDown()
        awaitIndependentTimerDispatch()

        assertEquals(1, executor.dispatches.get())
        assertFalse(ThreadUtils.TASK_POOL_MAP.containsKey(task))
    }

    @Test fun anAlreadyClosedExecutorDoesNotReceiveTheFirstDispatch() {
        val executor = executor()
        val task = task()
        executor.shutdown()

        ThreadUtils.executeByCustomAtFixRate(executor, task, 20L, TimeUnit.MILLISECONDS)
        awaitIndependentTimerDispatch()

        assertEquals(0, executor.dispatches.get())
    }

    @Test fun closingTheExecutorDuringDispatchStopsFutureDispatches() {
        val executor = executor(blockFirstDispatch = true)
        val task = task()
        ThreadUtils.executeByCustomAtFixRate(executor, task, 20L, TimeUnit.MILLISECONDS)
        assertTrue(executor.firstDispatch.await(5L, TimeUnit.SECONDS))

        executor.shutdown()
        executor.releaseFirstDispatch.countDown()
        awaitIndependentTimerDispatch()

        assertEquals(1, executor.dispatches.get())
    }

    @Test fun anOverflowingInitialDelayIsRejectedWithoutBreakingLaterDispatches() {
        val executor = executor()
        val task = task()

        assertThrows(IllegalArgumentException::class.java) {
            ThreadUtils.executeByCustomAtFixRate(
                executor, task, Long.MAX_VALUE, 20L, TimeUnit.MILLISECONDS
            )
        }

        assertFalse(ThreadUtils.TASK_POOL_MAP.containsKey(task))
        assertEquals(0, executor.dispatches.get())
        awaitIndependentTimerDispatch()
    }

    @Test fun theLargestPeriodCanBeCanceledWithoutBreakingLaterDispatches() {
        val executor = executor()
        val task = task { it.cancel(false) }

        ThreadUtils.executeByCustomAtFixRate(executor, task, Long.MAX_VALUE, TimeUnit.MILLISECONDS)

        assertTrue(task.firstRun.await(5L, TimeUnit.SECONDS))
        awaitIndependentTimerDispatch()
        assertEquals(1, executor.dispatches.get())
        assertFalse(ThreadUtils.TASK_POOL_MAP.containsKey(task))
    }

    private fun awaitIndependentTimerDispatch() {
        val probe = task { it.cancel(false) }
        ThreadUtils.executeByCustomWithDelay(executor(), probe, 50L, TimeUnit.MILLISECONDS)
        assertTrue(probe.firstRun.await(5L, TimeUnit.SECONDS))
    }

    private fun executor(blockFirstDispatch: Boolean = false): ControlledExecutor {
        return ControlledExecutor(blockFirstDispatch).also(executors::add)
    }

    private fun task(afterRun: (DispatchTask) -> Unit = {}): DispatchTask {
        return DispatchTask(afterRun).also(tasks::add)
    }

    /** 只验证调度器派发，不把覆写的执行方法当作后台结果回调验证。 */
    private class DispatchTask(private val afterRun: (DispatchTask) -> Unit) : Task<Unit>() {
        val firstRun = CountDownLatch(1)

        init {
            setDeliver(Executor { it.run() })
        }

        override fun run() {
            afterRun(this)
            firstRun.countDown()
        }

        override fun doInBackground() = Unit
        override fun onSuccess(result: Unit) = Unit
        override fun onCancel() = Unit
        override fun onFail(t: Throwable) = Unit
    }

    /** 同步记录派发并控制首次返回，不创建工作线程，也不向全局计时线程抛异常。 */
    private class ControlledExecutor(private val blockFirstDispatch: Boolean) : AbstractExecutorService() {
        val dispatches = AtomicInteger()
        val firstDispatch = CountDownLatch(1)
        val releaseFirstDispatch = CountDownLatch(1)
        private val stopped = AtomicBoolean()

        override fun execute(command: Runnable) {
            val count = dispatches.incrementAndGet()
            command.run()
            firstDispatch.countDown()
            if (blockFirstDispatch && count == 1) {
                try {
                    releaseFirstDispatch.await(5L, TimeUnit.SECONDS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }

        override fun shutdown() {
            stopped.set(true)
        }

        override fun shutdownNow(): MutableList<Runnable> {
            shutdown()
            releaseFirstDispatch.countDown()
            return mutableListOf()
        }

        override fun isShutdown(): Boolean = stopped.get()
        override fun isTerminated(): Boolean = stopped.get()
        override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = isTerminated
    }
}
