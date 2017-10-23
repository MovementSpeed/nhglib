package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.UiComponent;
import io.github.movementspeed.nhglib.data.models.serialization.AssetJson;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;

public class UiComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        UiComponent uiComponent = nhg.entities.createComponent(entity, UiComponent.class);
        uiComponent.fileName = jsonValue.getString("fileName", "");

        String uiType = jsonValue.getString("uiType", "screen").toLowerCase();

        if (uiType.contentEquals("screen")) {
            uiComponent.type = UiComponent.Type.SCREEN;
        } else if (uiType.contentEquals("panel")) {
            uiComponent.type = UiComponent.Type.PANEL;
        }

        JsonValue dependenciesJson = jsonValue.get("dependencies");

        for (int i = 0; i < dependenciesJson.size; i++) {
            AssetJson assetJson = new AssetJson();
            assetJson.parse(dependenciesJson.get(i));

            Asset dependency = assetJson.get();
            uiComponent.dependencies.add(dependency);
        }

        JsonValue actorNamesJson = jsonValue.get("actors");

        for (int i = 0; i < actorNamesJson.size; i++) {
            String actor = actorNamesJson.getString(i);
            uiComponent.actorNames.add(actor);
        }

        output = uiComponent;
    }
}