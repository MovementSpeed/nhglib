package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.tests.systems.TestNodeSystem;
import io.github.voidzombie.tests.systems.TestSystem;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NHGEntry {
    private Scene scene;
    private FPSLogger fpsLogger;

    @Override
    public void engineStarted() {
        super.engineStarted();
        NHG.debugLogs = true;

        fpsLogger = new FPSLogger();

        NHG.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);

                        if (asset.is("scene")) {
                            scene = NHG.assets.get(asset);
                            NHG.sceneManager.loadScene(scene);
                        }
                    }
                });
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();
    }

    @Override
    public void engineUpdate(float delta) {
        super.engineUpdate(delta);
        fpsLogger.log();

        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                    entity, NodeComponent.class);

            boolean input = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                nodeComponent.translate(0, 0, 0.1f * delta);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                nodeComponent.translate(0, 0, -0.1f * delta);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                nodeComponent.translate(-0.1f * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                nodeComponent.translate(0.1f * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                nodeComponent.rotate(0, -10f * delta, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                nodeComponent.rotate(0, 10f * delta, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                nodeComponent.rotate(-10 * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                nodeComponent.rotate(10 * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                nodeComponent.rotate(0, 0, -10 * delta);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.V)) {
                nodeComponent.rotate(0, 0, 10 * delta);
                input = true;
            }

            if (input) {
                nodeComponent.applyTransforms();
            }
        }
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
    }
}