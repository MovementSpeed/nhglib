package io.github.movementspeed.nhglib.core.entry

import com.artemis.BaseSystem
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.StateMachine
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.fsm.base.EngineStates
import io.github.movementspeed.nhglib.core.fsm.interfaces.EngineConfigurationListener
import io.github.movementspeed.nhglib.core.fsm.interfaces.EngineStateListener
import io.github.movementspeed.nhglib.core.messaging.Message
import io.github.movementspeed.nhglib.utils.data.Strings

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
internal abstract class BaseGame : ApplicationListener, EngineStateListener, EngineConfigurationListener {

    var nhg: Nhg

    private var width: Int = 0
    private var height: Int = 0
    private var fsm: DefaultStateMachine<NhgEntry, EngineStates>? = null

    override fun create() {
        nhg = Nhg()
        nhg.init()
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun render() {
        fsm!!.update()
    }

    override fun pause() {
        fsm!!.changeState(EngineStates.PAUSED)
    }

    override fun resume() {
        fsm!!.changeState(EngineStates.RUNNING)
    }

    override fun dispose() {
        nhg.messaging.send(Message(Strings.Events.engineDestroy))
        onDispose()
    }

    override fun onStart() {}

    override fun onInitialized() {
        onResize(width, height)
    }

    override fun onUpdate(delta: Float) {}

    override fun onPause() {

    }

    override fun onClose() {}

    override fun onResize(width: Int, height: Int) {

    }

    override fun onDispose() {

    }

    override fun onConfigureEntitySystems(): Array<BaseSystem> {
        return Array()
    }

    fun init(nhgEntry: NhgEntry) {
        fsm = DefaultStateMachine(nhgEntry, EngineStates.START)
    }

    fun getFsm(): StateMachine<NhgEntry, EngineStates>? {
        return fsm
    }
}
