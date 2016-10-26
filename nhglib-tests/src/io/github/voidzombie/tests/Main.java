package io.github.voidzombie.tests;

import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.runtime.BaseGame;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends BaseGame {
    @Override
    public void create() {
        super.create();

        NHG.assets.queueAsset(new Asset("weapon", "models/weapon.g3db", Model.class));
    }

    @Override
    public void update() {
        super.update();
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
