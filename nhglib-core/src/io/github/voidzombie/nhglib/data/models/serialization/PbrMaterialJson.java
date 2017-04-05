package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.graphics.utils.PbrMaterial;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class PbrMaterialJson implements JsonParseable<PbrMaterial> {
    private PbrMaterial pbrMaterial;

    @Override
    public void parse(JsonValue jsonValue) {
        pbrMaterial = new PbrMaterial();
        pbrMaterial.targetNode = jsonValue.getString("targetNode", "");

        JsonValue albedoJson = jsonValue.get("albedo");
        JsonValue metalnessJson = jsonValue.get("metalness");
        JsonValue roughnessJson = jsonValue.get("roughness");
        JsonValue normalJson = jsonValue.get("normal");
        JsonValue ambientOcclusionJson = jsonValue.get("ambientOcclusion");

        TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
        params.minFilter = Texture.TextureFilter.MipMap;
        params.magFilter = Texture.TextureFilter.Linear;
        params.genMipMaps = true;

        if (albedoJson != null) {
            AssetJson albedoAssetJson = new AssetJson();
            albedoAssetJson.parse(albedoJson.get("asset"));

            pbrMaterial.albedoAsset = albedoAssetJson.get();
            pbrMaterial.albedoAsset.parameters = params;
        }

        if (metalnessJson != null) {
            AssetJson metalnessAssetJson = new AssetJson();
            metalnessAssetJson.parse(metalnessJson.get("asset"));

            pbrMaterial.metalnessAsset = metalnessAssetJson.get();
            pbrMaterial.metalnessAsset.parameters = params;
        }

        if (roughnessJson != null) {
            AssetJson roughnessAssetJson = new AssetJson();
            roughnessAssetJson.parse(roughnessJson.get("asset"));

            pbrMaterial.roughnessAsset = roughnessAssetJson.get();
            pbrMaterial.roughnessAsset.parameters = params;
        }

        if (normalJson != null) {
            AssetJson normalAssetJson = new AssetJson();
            normalAssetJson.parse(normalJson.get("asset"));

            pbrMaterial.normalAsset = normalAssetJson.get();
            pbrMaterial.normalAsset.parameters = params;
        }

        if (ambientOcclusionJson != null) {
            AssetJson ambientOcclusionAssetJson = new AssetJson();
            ambientOcclusionAssetJson.parse(ambientOcclusionJson.get("asset"));

            pbrMaterial.ambientOcclusionAsset = ambientOcclusionAssetJson.get();
            pbrMaterial.ambientOcclusionAsset.parameters = params;
        }
    }

    @Override
    public PbrMaterial get() {
        return pbrMaterial;
    }
}
