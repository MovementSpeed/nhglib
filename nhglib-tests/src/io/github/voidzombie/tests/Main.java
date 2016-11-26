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
import io.github.voidzombie.nhglib.runtime.messaging.Message;
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
    public void engineStarted() {
        super.engineStarted();
        camera = new DefaultPerspectiveCamera();

        NHG.debugLogs = true;
        NHG.assets.queueAsset(new Asset("weapon", "models/weapon.g3db", Model.class));

        for (int i = 0; i < 123; i++) {
            int entity = createEntity();

            ObserverComponent observerComponent = createComponent(entity, ObserverComponent.class);
            observerComponent.listen(new Message("fire"));
        }
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();
    }

    @Override
    public void engineUpdate() {
        super.engineUpdate();
        camera.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            timeStart = System.currentTimeMillis();
            NHG.messaging.sendMessage("fire", null);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            NHG.messaging.sendMessage("fly", null);
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message.is(NHG.strings.events.assetLoaded)) {
            Asset asset = (Asset) message.data.get("asset");
            NHG.logger.log(this, asset.alias);
        } else if (message.is(NHG.strings.events.assetLoadingFinished)) {
            NHG.logger.log(this, "Loading finished!");
        }
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);

        configurationBuilder.with(new TestSystem());
    }
}