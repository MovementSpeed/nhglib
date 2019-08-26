package io.github.movementspeed.nhglib.core.threading

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.AbstractQueuedSynchronizer

class ResettableCountDownLatch(count: Int) {

    private val sync: Sync

    val count: Long
        get() = sync.count.toLong()

    /**
     * Synchronization control For CountDownLatch.
     * Uses AQS state to represent count.
     */
    private class Sync internal constructor(val startCount: Int) : AbstractQueuedSynchronizer() {

        internal val count: Int
            get() = state

        init {
            state = startCount
        }

        public override fun tryAcquireShared(acquires: Int): Int {
            return if (state == 0) 1 else -1
        }

        public override fun tryReleaseShared(releases: Int): Boolean {
            // Decrement count; signal when transition to zero
            while (true) {
                val c = state
                if (c == 0)
                    return false
                val nextc = c - 1
                if (compareAndSetState(c, nextc))
                    return nextc == 0
            }
        }

        fun reset() {
            state = startCount
        }

        companion object {
            private val serialVersionUID = 4982264981922014374L
        }
    }

    init {
        require(count >= 0) { "count < 0" }
        this.sync = Sync(count)
    }

    @Throws(InterruptedException::class)
    fun await() {
        sync.acquireSharedInterruptibly(1)
    }

    fun reset() {
        sync.reset()
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout))
    }

    fun countDown() {
        sync.releaseShared(1)
    }

    override fun toString(): String {
        return super.toString() + "[Count = " + sync.count + "]"
    }
}