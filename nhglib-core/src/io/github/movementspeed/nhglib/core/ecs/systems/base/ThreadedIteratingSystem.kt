package io.github.movementspeed.nhglib.core.ecs.systems.base

import com.artemis.Aspect
import com.artemis.utils.IntBag
import com.badlogic.gdx.math.MathUtils
import io.github.movementspeed.nhglib.core.threading.Threading
import io.github.movementspeed.nhglib.core.threading.Work

import java.util.Arrays

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
abstract class ThreadedIteratingSystem(aspect: Aspect.Builder, private val threading: Threading) : NhgBaseEntitySystem(aspect) {
    private var split: Int = 0
    private var rows: Int = 0
    private val latchId: Int

    private var splitEntities: Array<IntArray>? = null

    init {

        latchId = 2165
        threading.createLatch(latchId, Threading.cores)
    }

    override fun processSystem() {
        val actives = subscription.entities
        val activesSize = actives.size()

        if (activesSize > 0) {
            val previousSplit = split
            split = MathUtils.ceil(actives.size().toFloat() / Threading.cores.toFloat())

            val previousRows = rows
            if (activesSize > Threading.cores) {
                rows = Threading.cores
            } else {
                rows = activesSize
            }

            if (previousRows != rows) {
                threading.setLatchCount(latchId, rows)
            }

            if (previousRows != rows || previousSplit != split) {
                splitEntities = Array(rows) { IntArray(split) }

                var from: Int
                var to: Int
                val data = actives.data

                for (i in 0 until rows) {
                    if (split == 1) {
                        splitEntities!![i][0] = data[i]
                    } else {
                        from = i * split
                        to = from + split

                        splitEntities[i] = Arrays.copyOfRange(data, from, to)
                    }

                    if (i > 0) {
                        postProcessList(splitEntities!![i])
                    }
                }
            }

            for (i in 0 until rows) {
                threading.execute(ProcessWork(splitEntities!![i]))
            }

            threading.awaitLatch(latchId)
        }
    }

    protected abstract fun process(entityId: Int)

    private fun postProcessList(list: IntArray) {
        for (i in list.indices) {
            val entity = list[i]

            if (entity == 0) {
                list[i] = -1
            }
        }
    }

    private inner class ProcessWork internal constructor(private val entitiesPart: IntArray?) : Work() {

        override fun run() {
            if (entitiesPart != null) {
                for (entity in entitiesPart) {
                    if (entity != -1) {
                        process(entity)
                    }
                }
            }

            threading.countDownLatch(latchId)
        }
    }
}