package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.entry.BaseGame;
import io.github.voidzombie.nhglib.runtime.messaging.Event;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.tests.systems.TestSystem;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends BaseGame {
    public static long timeStart;
    public static long timeEnd;
    public static long average;
    private DefaultPerspectiveCamera camera;

    @Override
    public void onEngineStart() {
        super.onEngineStart();
        camera = new DefaultPerspectiveCamera();

        NHG.debugLogs = true;
        NHG.assets.queueAsset(new Asset("weapon", "models/weapon.g3db", Model.class));

        for (int i = 0; i < 5; i++) {
            int entity = createEntity();

            ObserverComponent observerComponent = createComponent(entity, ObserverComponent.class);
            observerComponent.subscribe(new Event("fire"));
        }
    }

    @Override
    public void onEngineInitialized() {
        super.onEngineInitialized();
    }

    @Override
    public void onEngineRunning() {
        super.onEngineRunning();
        camera.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            timeStart = System.currentTimeMillis();
            NHG.messaging.broadcastEvent("fire", null);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            NHG.messaging.broadcastEvent("fly", null);
        }
    }

    @Override
    public void onLoadingCompleted() {
        super.onLoadingCompleted();
    }

    @Override
    public void onAssetLoaded(Asset asset) {
        super.onAssetLoaded(asset);
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);

        configurationBuilder.with(new TestSystem());
    }
}
