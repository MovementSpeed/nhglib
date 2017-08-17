package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.graphics.utils.PbrMaterial;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class PbrMaterialJson implements JsonParseable<PbrMaterial> {
    private PbrMaterial pbrMaterial;

    @Override
    public void parse(JsonValue jsonValue) {
        pbrMaterial = new PbrMaterial();
        pbrMaterial.targetNode = jsonValue.getString("targetNode", "");

        String albedo = jsonValue.getString("albedo", "");
        String metalness = jsonValue.getString("metalness", "");
        String roughness = jsonValue.getString("roughness", "");
        String normal = jsonValue.getString("normal", "");
        String ambientOcclusion = jsonValue.getString("ambientOcclusion", "");

        if (albedo != null) {
            pbrMaterial.albedo = albedo;
        }

        if (metalness != null) {
            pbrMaterial.metalness = metalness;
        }

        if (roughness != null) {
            pbrMaterial.roughness = roughness;
        }

        if (normal != null) {
            pbrMaterial.normal = normal;
        }

        if (ambientOcclusion != null) {
            pbrMaterial.ambientOcclusion = ambientOcclusion;
        }
    }

    @Override
    public PbrMaterial get() {
        return pbrMaterial;
    }
}
