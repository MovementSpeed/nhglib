package io.github.voidzombie.nhglib.data.models.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.data.models.AssetJson;
import io.github.voidzombie.nhglib.data.models.ComponentJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class GraphicsComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        GraphicsComponent graphicsComponent =
                NHG.entitySystem.createComponent(entity, GraphicsComponent.class);

        JsonValue asset = jsonValue.get("asset");

        AssetJson assetJson = new AssetJson();
        assetJson.parse(asset);

        graphicsComponent.asset = assetJson.get();
        output = graphicsComponent;
    }
}
