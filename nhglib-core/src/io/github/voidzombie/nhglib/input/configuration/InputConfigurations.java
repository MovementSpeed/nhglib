package io.github.voidzombie.nhglib.input.configuration;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.input.configuration.impls.KeyInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.PointerInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.StickInputConfiguration;
import io.github.voidzombie.nhglib.input.controllers.ControllerConfiguration;

/**
 * Created by worse on 25/01/2017.
 */
public class InputConfigurations {
    public final Array<KeyInputConfiguration> keyInputConfigurations;
    public final Array<PointerInputConfiguration> pointerInputConfigurations;
    public final Array<StickInputConfiguration> stickInputConfigurations;
    public final Array<ControllerConfiguration> controllerConfigurations;

    public InputConfigurations() {
        keyInputConfigurations = new Array<>();
        pointerInputConfigurations = new Array<>();
        stickInputConfigurations = new Array<>();
        controllerConfigurations = new Array<>();
    }

    public KeyInputConfiguration getKeyConfiguration(String name) {
        KeyInputConfiguration res = null;

        for (KeyInputConfiguration configuration : keyInputConfigurations) {
            if (configuration.getName().contentEquals(name)) {
                res = configuration;
            }
        }

        return res;
    }

    public PointerInputConfiguration getPointerConfiguration(String name) {
        PointerInputConfiguration res = null;

        for (PointerInputConfiguration configuration : pointerInputConfigurations) {
            if (configuration.getName().contentEquals(name)) {
                res = configuration;
            }
        }

        return res;
    }

    public StickInputConfiguration getStickConfiguration(String name) {
        StickInputConfiguration res = null;

        for (StickInputConfiguration configuration : stickInputConfigurations) {
            if (configuration.getName().contentEquals(name)) {
                res = configuration;
            }
        }

        return res;
    }

    public ControllerConfiguration getControllerConfiguration(Integer id) {
        ControllerConfiguration res = null;

        for (ControllerConfiguration configuration : controllerConfigurations) {
            if (configuration.getId().compareTo(id) == 0) {
                res = configuration;
            }
        }

        return res;
    }
}
