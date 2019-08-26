package io.github.movementspeed.nhglib.core.threading

import com.badlogic.gdx.utils.ArrayMap

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Fausto Napoli on 05/11/2016.
 */
class Threading {

    private val executor: ExecutorService
    private val latches: ArrayMap<Int, ResettableCountDownLatch>

    init {
        executor = Executors.newFixedThreadPool(cores)
        latches = ArrayMap()
    }

    fun execute(work: Work) {
        executor.execute(work)
    }

    fun createLatch(latchId: Int, count: Int) {
        latches.put(latchId, ResettableCountDownLatch(count))
    }

    fun awaitLatch(latchId: Int) {
        if (latches.containsKey(latchId)) {
            val latch = latches.get(latchId)

            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            latch.reset()
        }
    }

    fun countDownLatch(latchId: Int) {
        if (latches.containsKey(latchId)) {
            latches.get(latchId).countDown()
        }
    }

    fun setLatchCount(latchId: Int, count: Int) {
        createLatch(latchId, count)
    }

    fun terminate() {
        executor.shutdown()
    }

    companion object {
        val cores = Runtime.getRuntime().availableProcessors()
    }
}