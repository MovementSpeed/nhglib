package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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

    @Override
    public void engineStarted() {
        super.engineStarted();
        NHG.debugLogs = true;

        scene = new Scene();

        /*scene = new Scene();

        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.ColorUnpacked |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.Position);

        // Root scene entity
        int rootSceneEntity = scene.sceneGraph.getRootEntity();

        MessageComponent rootMessageComponent = NHG.entitySystem.createComponent(
                rootSceneEntity, MessageComponent.class);
        rootMessageComponent.subscribe("printNode");

        NodeComponent rootNodeComponent = NHG.entitySystem.getComponent(rootSceneEntity, NodeComponent.class);
        rootNodeComponent.translate(0, -0.2f, 0, true);

        GraphicsComponent rootGraphicsComponent = NHG.entitySystem.createComponent(
                rootSceneEntity, GraphicsComponent.class);
        rootGraphicsComponent.setRepresentation(new ModelRepresentation(box));

        // First scene entity
        int firstSceneEntity = scene.sceneGraph.addSceneEntity();

        MessageComponent firstMessageComponent = NHG.entitySystem.createComponent(
                firstSceneEntity, MessageComponent.class);
        firstMessageComponent.subscribe("printNode");

        NodeComponent firstNodeComponent = NHG.entitySystem.getComponent(
                firstSceneEntity, NodeComponent.class);
        firstNodeComponent.translate(0, 0.1f, 0, true);

        GraphicsComponent firstGraphicsComponent = NHG.entitySystem.createComponent(
                firstSceneEntity, GraphicsComponent.class);
        firstGraphicsComponent.asset =
                new Asset("firstAsset", "models/weapon.g3db", Model.class);

        // Second scene entity
        int secondSceneEntity = scene.sceneGraph.addSceneEntity(firstSceneEntity);

        MessageComponent secondMessageComponent = NHG.entitySystem.createComponent(
                secondSceneEntity, MessageComponent.class);
        secondMessageComponent.subscribe("printNode");

        NodeComponent secondNodeComponent = NHG.entitySystem.getComponent(
                secondSceneEntity, NodeComponent.class);
        secondNodeComponent.translate(0, 0.2f, 0.1f, true);

        GraphicsComponent secondGraphicsComponent = NHG.entitySystem.createComponent(
                secondSceneEntity, GraphicsComponent.class);
        secondGraphicsComponent.asset =
                new Asset("secondAsset", "models/weapon.g3db", Model.class);

        for (int i = 0; i < 10; i++) {
            newEntity(MathUtils.random(-2f, 2f), MathUtils.random(0f, 0.5f), MathUtils.random(-2, 2f));
        }*/

        //NHG.sceneManager.loadScene(scene);

        NHG.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                        NHG.logger.log(this, "Loaded asset with alias: %s", asset.alias);

                        if (asset.is("scene")) {
                            scene = NHG.assets.get(asset);
                            NHG.sceneManager.loadScene(scene);
                        }
                    } else if (message.is(NHG.strings.events.assetLoadingFinished)) {
                        NHG.logger.log(this, "Loading finished!");
                    }
                });
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();
    }

    @Override
    public void engineUpdate() {
        super.engineUpdate();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Bundle data = new Bundle();
            data.put("action", "fire");

            NHG.messaging.send(new Message("fire", data));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            NHG.messaging.send(new Message("fly"));
        }

        if (scene != null) {
            int rootSceneEntity = scene.sceneGraph.getRootEntity();
            NodeComponent rootNodeComponent = NHG.entitySystem.getComponent(
                    rootSceneEntity, NodeComponent.class);

            boolean input = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                rootNodeComponent.translate(0, 0, 0.01f);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                rootNodeComponent.translate(0, 0, -0.01f);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                rootNodeComponent.translate(-0.01f, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                rootNodeComponent.translate(0.01f, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                rootNodeComponent.rotate(0, -1f, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                rootNodeComponent.rotate(0, 1f, 0);
                input = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                newEntity(MathUtils.random(-0.3f, 0.3f), MathUtils.random(0f, 0.5f), MathUtils.random(-0.3f, 0.3f));
                NHG.sceneManager.refresh();
            }

            if (input) {
                rootNodeComponent.applyTransforms();
            }
        }
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
        configurationBuilder.with(new TestSystem());
        configurationBuilder.with(new TestNodeSystem());
    }

    private void newEntity(float x, float y, float z) {
        int entity = scene.sceneGraph.addSceneEntity(scene.sceneGraph.getRootEntity());

        NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                entity, NodeComponent.class);
        nodeComponent.setTranslation(x, y, z, true);

        GraphicsComponent graphicsComponent = NHG.entitySystem.createComponent(
                entity, GraphicsComponent.class);
        graphicsComponent.asset =
                new Asset("" + entity, "models/weapon.g3db", Model.class);
    }
}