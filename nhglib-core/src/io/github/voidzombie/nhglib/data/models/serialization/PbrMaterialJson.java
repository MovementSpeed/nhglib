package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class PbrMaterialJson implements JsonParseable<PbrMaterialJson> {
    public String targetNode;

    public Asset albedoAsset;
    public Asset metalnessAsset;
    public Asset roughnessAsset;
    public Asset normalAsset;
    public Asset ambientOcclusionAsset;

    @Override
    public void parse(JsonValue jsonValue) {
        targetNode = jsonValue.getString("targetNode");

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
            albedoAsset = albedoAssetJson.get();
            albedoAsset.parameters = params;
        }

        if (metalnessJson != null) {
            AssetJson metalnessAssetJson = new AssetJson();
            metalnessAssetJson.parse(metalnessJson.get("asset"));
            metalnessAsset = metalnessAssetJson.get();
            metalnessAsset.parameters = params;
        }

        if (roughnessJson != null) {
            AssetJson roughnessAssetJson = new AssetJson();
            roughnessAssetJson.parse(roughnessJson.get("asset"));
            roughnessAsset = roughnessAssetJson.get();
            roughnessAsset.parameters = params;
        }

        if (normalJson != null) {
            AssetJson normalAssetJson = new AssetJson();
            normalAssetJson.parse(normalJson.get("asset"));
            normalAsset = normalAssetJson.get();
            normalAsset.parameters = params;
        }

        if (ambientOcclusionJson != null) {
            AssetJson ambientOcclusionAssetJson = new AssetJson();
            ambientOcclusionAssetJson.parse(ambientOcclusionJson.get("asset"));
            ambientOcclusionAsset = ambientOcclusionAssetJson.get();
            ambientOcclusionAsset.parameters = params;
        }
    }

    @Override
    public PbrMaterialJson get() {
        return this;
    }
}
