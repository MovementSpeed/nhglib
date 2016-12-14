package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.graphics.utils.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.scenes.SceneGraph;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.tests.systems.TestNodeSystem;
import io.github.voidzombie.tests.systems.TestSystem;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NHGEntry {
    private int firstSceneEntity;
    private SceneGraph sceneGraph;

    @Override
    public void engineStarted() {
        super.engineStarted();

        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.ColorUnpacked |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.Position);

        Model sphere = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 16, 16,
                new Material(ColorAttribute.createDiffuse(Color.CHARTREUSE)),
                VertexAttributes.Usage.ColorUnpacked |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.Position);
        
        NHG.debugLogs = true;
        NHG.assets.queueAsset(new Asset("weapon", "models/weapon.g3db", Model.class));

        for (int i = 0; i < 5; i++) {
            int entity = NHG.entitySystem.createEntity();

            MessageComponent messageComponent = NHG.entitySystem.createComponent(
                    entity, MessageComponent.class);
            messageComponent.subscribe("fire", "fly");
        }

        sceneGraph = new SceneGraph();

        // Root scene entity
        int rootSceneEntity = sceneGraph.getRootEntity();

        MessageComponent rootMessageComponent = NHG.entitySystem.createComponent(
                rootSceneEntity, MessageComponent.class);
        rootMessageComponent.subscribe("printNode");

        NodeComponent rootNodeComponent = NHG.entitySystem.getComponent(rootSceneEntity, NodeComponent.class);
        rootNodeComponent.translate(0, -0.2f, 0, true);

        GraphicsComponent rootGraphicsComponent = NHG.entitySystem.createComponent(
                rootSceneEntity, GraphicsComponent.class);
        rootGraphicsComponent.representation = new ModelRepresentation(box);

        // First scene entity
        firstSceneEntity = sceneGraph.addSceneEntity();

        MessageComponent firstMessageComponent = NHG.entitySystem.createComponent(
                firstSceneEntity, MessageComponent.class);
        firstMessageComponent.subscribe("printNode");

        NodeComponent firstNodeComponent = NHG.entitySystem.getComponent(
                firstSceneEntity, NodeComponent.class);
        firstNodeComponent.translate(0, 0.1f, 0, true);

        GraphicsComponent firstGraphicsComponent = NHG.entitySystem.createComponent(
                firstSceneEntity, GraphicsComponent.class);
        firstGraphicsComponent.representation = new ModelRepresentation(sphere);

        // Second scene entity
        int secondSceneEntity = sceneGraph.addSceneEntity(firstSceneEntity);

        MessageComponent secondMessageComponent = NHG.entitySystem.createComponent(
                secondSceneEntity, MessageComponent.class);
        secondMessageComponent.subscribe("printNode");

        NodeComponent secondNodeComponent = NHG.entitySystem.getComponent(
                secondSceneEntity, NodeComponent.class);
        secondNodeComponent.translate(0, 0.2f, 0.1f, true);

        GraphicsComponent secondGraphicsComponent = NHG.entitySystem.createComponent(
                secondSceneEntity, GraphicsComponent.class);
        secondGraphicsComponent.representation = new ModelRepresentation(sphere);

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get("asset");
                        NHG.logger.log(this, asset.alias);

                        GraphicsComponent graphicsComponent = NHG.entitySystem.getComponent(
                                firstSceneEntity, GraphicsComponent.class);

                        graphicsComponent.representation = new ModelRepresentation(NHG.assets.get(asset));
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

        int rootSceneEntity = sceneGraph.getRootEntity();
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

        if (input) {
            rootNodeComponent.applyTransforms();
        }
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
        configurationBuilder.with(new TestSystem());
        configurationBuilder.with(new TestNodeSystem());
    }
}