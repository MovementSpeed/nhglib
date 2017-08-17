package io.github.movementspeed.nhglib.runtime.fsm.interfaces;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineConfigurationListener {
    Array<BaseSystem> onConfigureEntitySystems();
}
