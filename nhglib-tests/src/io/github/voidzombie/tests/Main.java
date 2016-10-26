package io.github.voidzombie.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.BaseGame;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends BaseGame {
    private DefaultPerspectiveCamera camera;

    @Override
    public void create() {
        super.create();

        camera = new DefaultPerspectiveCamera();

        NHG.debugLogs = true;
        NHG.assets.queueAsset(new Asset("weapon", "models/weapon.g3db", Model.class));
    }

    @Override
    public void update() {
        super.update();
        camera.update();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void onLoadingCompleted() {
        super.onLoadingCompleted();
    }

    @Override
    public void onAssetLoaded(Asset asset) {
        super.onAssetLoaded(asset);
    }
}
