package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class MessageComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        MessageComponent messageComponent =
                Nhg.entitySystem.createComponent(entity, MessageComponent.class);

        JsonValue filters = jsonValue.get("filters");
        messageComponent.subscribe(filters.asStringArray());

        output = messageComponent;
    }
}
