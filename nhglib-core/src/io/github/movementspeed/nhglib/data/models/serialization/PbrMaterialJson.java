package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.graphics.Color;
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

        if (jsonValue.has("metalnessValue")) {
            pbrMaterial.metalnessValue = jsonValue.getFloat("metalnessValue", 0.001f);
        } else {
            pbrMaterial.metalnessValue = -1;
        }

        if (jsonValue.has("roughnessValue")) {
            pbrMaterial.roughnessValue = jsonValue.getFloat("roughnessValue", 0.5f);
        } else {
            pbrMaterial.roughnessValue = -1;
        }

        JsonValue albedoColorJson = jsonValue.get("albedoColor");

        if (albedoColorJson != null) {
            pbrMaterial.albedoColor = new Color(
                    albedoColorJson.getFloat("r", 1.0f),
                    albedoColorJson.getFloat("g", 1.0f),
                    albedoColorJson.getFloat("b", 1.0f),
                    albedoColorJson.getFloat("a", 1.0f));
        }

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
