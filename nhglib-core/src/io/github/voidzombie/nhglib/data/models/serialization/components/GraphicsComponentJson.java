package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.data.models.serialization.AssetJson;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class GraphicsComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        GraphicsComponent graphicsComponent =
                NHG.entitySystem.createComponent(entity, GraphicsComponent.class);

        String type = jsonValue.getString("graphicsType");
        JsonValue asset = jsonValue.get("asset");

        AssetJson assetJson = new AssetJson();
        assetJson.parse(asset);

        graphicsComponent.type = GraphicsComponent.Type.fromString(type);
        graphicsComponent.asset = assetJson.get();
        output = graphicsComponent;
    }
}
