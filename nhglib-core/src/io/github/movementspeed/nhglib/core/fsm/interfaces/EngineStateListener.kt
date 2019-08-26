package io.github.movementspeed.nhglib.core.fsm.interfaces

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
interface EngineStateListener {
    fun onStart()

    fun onInitialized()

    fun onUpdate(delta: Float)

    fun onPause()

    fun onClose()

    fun onResize(width: Int, height: Int)

    fun onDispose()
}
