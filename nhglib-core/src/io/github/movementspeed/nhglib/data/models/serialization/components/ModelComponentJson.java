package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;
import io.github.movementspeed.nhglib.data.models.serialization.PbrMaterialJson;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class ModelComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        ModelComponent modelComponent =
                nhg.entities.createComponent(entity, ModelComponent.class);

        String type = jsonValue.getString("graphicsType");
        String asset = jsonValue.getString("asset", "");

        boolean enabled = jsonValue.getBoolean("enabled", true);

        JsonValue materialsJson = jsonValue.get("materials");

        if (materialsJson != null) {
            for (JsonValue mat : materialsJson) {
                PbrMaterialJson pbrMaterialJson = new PbrMaterialJson();
                pbrMaterialJson.parse(mat);

                modelComponent.pbrMaterials.add(pbrMaterialJson.get());
            }
        }

        modelComponent.type = ModelComponent.Type.fromString(type);
        modelComponent.asset = asset;
        modelComponent.enabled = enabled;
        output = modelComponent;
    }
}
