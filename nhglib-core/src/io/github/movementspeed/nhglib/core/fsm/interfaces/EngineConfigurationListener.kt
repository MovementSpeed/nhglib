package io.github.movementspeed.nhglib.core.fsm.interfaces

import com.artemis.BaseSystem
import com.badlogic.gdx.utils.Array

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
interface EngineConfigurationListener {
    fun onConfigureEntitySystems(): Array<BaseSystem>
}
