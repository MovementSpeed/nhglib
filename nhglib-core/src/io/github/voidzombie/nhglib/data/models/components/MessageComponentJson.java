package io.github.voidzombie.nhglib.data.models.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.ComponentJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class MessageComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        MessageComponent messageComponent =
                NHG.entitySystem.createComponent(entity, MessageComponent.class);

        JsonValue filters = jsonValue.get("filters");
        messageComponent.subscribe(filters.asStringArray());

        output = messageComponent;
    }
}
