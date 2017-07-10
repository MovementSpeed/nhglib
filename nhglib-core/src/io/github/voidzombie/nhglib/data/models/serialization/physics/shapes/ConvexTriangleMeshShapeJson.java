package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.assets.loaders.NhgG3dModelLoader;
import io.github.voidzombie.nhglib.data.models.serialization.AssetJson;

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
public class ConvexTriangleMeshShapeJson extends ShapeJson {
    private btCollisionShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        AssetJson assetJson = new AssetJson();
        assetJson.parse(jsonValue.get("asset"));

        boolean calcAabb = jsonValue.getBoolean("calcAabb", true);

        Asset asset = assetJson.get();

        NhgG3dModelLoader modelLoader = new NhgG3dModelLoader(nhg.assets, new UBJsonReader());
        modelLoader.setCurrentAsset(asset);

        Model model = modelLoader.loadModel(Gdx.files.internal(asset.source));
        shape = new btConvexTriangleMeshShape(btTriangleIndexVertexArray.obtain(model.meshParts), calcAabb);

    }

    @Override
    public btCollisionShape get() {
        return shape;
    }
}
