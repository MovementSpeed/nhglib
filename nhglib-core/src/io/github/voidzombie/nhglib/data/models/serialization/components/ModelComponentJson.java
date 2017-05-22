package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.AssetJson;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.PbrMaterialJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class ModelComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        ModelComponent modelComponent =
                entities.createComponent(entity, ModelComponent.class);

        String type = jsonValue.getString("graphicsType");
        JsonValue asset = jsonValue.get("asset");

        boolean enabled = jsonValue.getBoolean("enabled", true);

        AssetJson assetJson = new AssetJson();
        assetJson.parse(asset);

        JsonValue materialsJson = jsonValue.get("materials");

        if (materialsJson != null) {
            for (JsonValue mat : materialsJson) {
                PbrMaterialJson pbrMaterialJson = new PbrMaterialJson();
                pbrMaterialJson.parse(mat);

                modelComponent.pbrMaterials.add(pbrMaterialJson.get());
            }
        }

        modelComponent.type = ModelComponent.Type.fromString(type);
        modelComponent.asset = assetJson.get();
        modelComponent.enabled = enabled;
        output = modelComponent;
    }
}
