package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.input.models.InputContext;
import io.github.movementspeed.nhglib.input.models.InputType;
import io.github.movementspeed.nhglib.input.models.NhgInput;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputJson implements JsonParseable<Array<InputContext>> {
    private Array<InputContext> inputContexts;

    public InputJson() {
        inputContexts = new Array<>();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        JsonValue contextsJson = jsonValue.get("contexts");

        for (JsonValue contextJson : contextsJson) {
            String name = contextJson.getString("name");
            JsonValue inputsJson = contextJson.get("inputs");

            InputContext inputContext = new InputContext(name);

            for (JsonValue inputJson : inputsJson) {
                inputContext.addInput(inputFromJson(inputJson));
            }

            inputContexts.add(inputContext);
        }
    }

    @Override
    public Array<InputContext> get() {
        return inputContexts;
    }

    private NhgInput inputFromJson(JsonValue inputJson) {
        String inputName = inputJson.getString("name");
        String inputTypeString = inputJson.getString("type");

        InputType inputType = InputType.fromString(inputTypeString);

        NhgInput input = new NhgInput(inputName);
        input.setType(inputType);

        return input;
    }
}
