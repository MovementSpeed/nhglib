package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.data.models.serialization.AssetJson;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.UiComponent;

public class UiComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        UiComponent uiComponent = nhg.entities.createComponent(entity, UiComponent.class);
        uiComponent.fileName = jsonValue.getString("fileName", "");

        JsonValue dependenciesJson = jsonValue.get("dependencies");

        for (int i = 0; i < dependenciesJson.size; i++) {
            AssetJson assetJson = new AssetJson();
            assetJson.parse(dependenciesJson.get(i));

            Asset dependency = assetJson.get();
            uiComponent.dependencies.add(dependency);
        }

        output = uiComponent;
    }
}