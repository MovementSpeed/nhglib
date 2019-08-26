package io.github.movementspeed.nhglib.core.entry

import com.artemis.BaseSystem
import com.badlogic.gdx.utils.Array

/**
 * Created by Fausto Napoli on 26/11/2016.
 * Public entry point for games using this library.
 */
open class NhgEntry : BaseGame() {
    init {
        init(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onInitialized() {
        super.onInitialized()
    }

    override fun onUpdate(delta: Float) {
        super.onUpdate(delta)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClose() {
        super.onClose()
    }

    override fun onResize(width: Int, height: Int) {
        super.onResize(width, height)
    }

    override fun onDispose() {
        super.onDispose()
    }

    override fun onConfigureEntitySystems(): Array<BaseSystem> {
        return super.onConfigureEntitySystems()
    }
}
