package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.input.configuration.InputConfigurations;
import io.github.voidzombie.nhglib.input.configuration.impls.KeyInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.MouseInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.PointerInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.StickInputConfiguration;
import io.github.voidzombie.nhglib.input.controllers.ControllerConfiguration;
import io.github.voidzombie.nhglib.input.controllers.StickConfiguration;
import io.github.voidzombie.nhglib.input.models.InputMode;
import io.github.voidzombie.nhglib.input.models.MouseSourceType;
import io.github.voidzombie.nhglib.input.models.PointerSourceType;
import io.github.voidzombie.nhglib.input.models.StickType;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 25/01/2017.
 */
public class InputConfigurationsJson implements JsonParseable<InputConfigurations> {
    private InputConfigurations inputConfigurations;

    @Override
    public void parse(JsonValue jsonValue) {
        inputConfigurations = new InputConfigurations();
        JsonValue configurationsJson = jsonValue.get("configurations");

        JsonValue keysJson = configurationsJson.get("keys");
        JsonValue sticksJson = configurationsJson.get("sticks");
        JsonValue pointersJson = configurationsJson.get("pointers");
        JsonValue controllersJson = configurationsJson.get("controllers");
        JsonValue mouseJson = configurationsJson.get("mouse");

        // Keys
        for (JsonValue keyJson : keysJson) {
            String name = keyJson.getString("name");
            Integer keyCode = keyJson.getInt("keyCode");
            InputMode inputMode = InputMode.fromString(keyJson.getString("inputMode"));

            KeyInputConfiguration keyInputConfiguration = new KeyInputConfiguration();
            keyInputConfiguration.setName(name);
            keyInputConfiguration.setKeyCode(keyCode);
            keyInputConfiguration.setInputMode(inputMode);

            inputConfigurations.keyInputConfigurations.add(keyInputConfiguration);
        }

        // Sticks
        for (JsonValue stickJson : sticksJson) {
            String name = stickJson.getString("name");
            Integer controllerId = stickJson.getInt("controllerId");
            StickType stickType = StickType.fromString(stickJson.getString("stickType"));

            StickInputConfiguration stickInputConfiguration = new StickInputConfiguration();
            stickInputConfiguration.setName(name);
            stickInputConfiguration.setControllerId(controllerId);
            stickInputConfiguration.setStickType(stickType);

            inputConfigurations.stickInputConfigurations.add(stickInputConfiguration);
        }

        // Pointers
        for (JsonValue pointerJson : pointersJson) {
            Integer pointerId = pointerJson.getInt("id");
            String name = pointerJson.getString("name");

            Float horizontalSensitivity = pointerJson.getFloat("horizontalSensitivity");
            Float verticalSensitivity = pointerJson.getFloat("verticalSensitivity");

            PointerSourceType pointerSourceType = PointerSourceType.fromString(pointerJson.getString("sourceType"));

            PointerInputConfiguration pointerInputConfiguration = new PointerInputConfiguration();
            pointerInputConfiguration.setId(pointerId);
            pointerInputConfiguration.setName(name);
            pointerInputConfiguration.setHorizontalSensitivity(horizontalSensitivity);
            pointerInputConfiguration.setVerticalSensitivity(verticalSensitivity);
            pointerInputConfiguration.setPointerSourceType(pointerSourceType);

            inputConfigurations.pointerInputConfigurations.add(pointerInputConfiguration);
        }

        // Controllers
        for (JsonValue controllerJson : controllersJson) {
            Integer id = controllerJson.getInt("id");

            JsonValue leftStickConfigurationJson = controllerJson.get("leftStick");
            JsonValue rightStickConfigurationJson = controllerJson.get("rightStick");

            StickConfiguration leftStick = stickConfigurationFromJson(leftStickConfigurationJson);
            StickConfiguration rightStick = stickConfigurationFromJson(rightStickConfigurationJson);

            ControllerConfiguration controllerConfiguration = new ControllerConfiguration();
            controllerConfiguration.setId(id);
            controllerConfiguration.setLeftStick(leftStick);
            controllerConfiguration.setRightStick(rightStick);

            inputConfigurations.controllerConfigurations.add(controllerConfiguration);
        }

        // Mouse
        for (JsonValue mouse : mouseJson) {
            Float mouseHorizontalSensitivity = mouse.getFloat("horizontalSensitivity", 0f);
            Float mouseVerticalSensitivity = mouse.getFloat("verticalSensitivity", 0f);

            String name = mouse.getString("name");
            MouseSourceType mouseSourceType = MouseSourceType.fromString(mouse.getString("sourceType"));

            MouseInputConfiguration mouseInputConfiguration = new MouseInputConfiguration();
            mouseInputConfiguration.setName(name);
            mouseInputConfiguration.setHorizontalSensitivity(mouseHorizontalSensitivity);
            mouseInputConfiguration.setVerticalSensitivity(mouseVerticalSensitivity);
            mouseInputConfiguration.setMouseSourceType(mouseSourceType);

            inputConfigurations.mouseInputConfigurations.add(mouseInputConfiguration);
        }
    }

    @Override
    public InputConfigurations get() {
        return inputConfigurations;
    }

    private StickConfiguration stickConfigurationFromJson(JsonValue stickConfigurationJson) {
        Boolean invertHorizontalAxis = stickConfigurationJson.getBoolean("invertHorizontalAxis");
        Boolean invertVerticalAxis = stickConfigurationJson.getBoolean("invertVerticalAxis");

        Float deadZoneRadius = stickConfigurationJson.getFloat("deadZoneRadius");
        Float horizontalSensitivity = stickConfigurationJson.getFloat("horizontalSensitivity");
        Float verticalSensitivity = stickConfigurationJson.getFloat("verticalSensitivity");

        StickConfiguration stickConfiguration = new StickConfiguration();
        stickConfiguration.setInvertHorizontalAxis(invertHorizontalAxis);
        stickConfiguration.setInvertVerticalAxis(invertVerticalAxis);
        stickConfiguration.setDeadZoneRadius(deadZoneRadius);
        stickConfiguration.setHorizontalSensitivity(horizontalSensitivity);
        stickConfiguration.setVerticalSensitivity(verticalSensitivity);

        return stickConfiguration;
    }
}
