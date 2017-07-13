package io.github.voidzombie.nhglib.runtime.fsm.interfaces;

import com.artemis.BaseEntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineConfigurationListener {
    Array<BaseEntitySystem> onConfigureEntitySystems();
}
