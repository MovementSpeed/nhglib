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
import io.github.voidzombie.nhglib.graphics.utils.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
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
    private SceneGraph sceneGraph;
    private ModelBatch modelBatch;
    private ModelInstance plane;
    private DefaultPerspectiveCamera camera;

    @Override
    public void engineStarted() {
        super.engineStarted();
        camera = new DefaultPerspectiveCamera();
        modelBatch = new ModelBatch();

        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createBox(1f, 0.1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.ColorUnpacked |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.Position);

        plane = new ModelInstance(box);
        plane.transform.translate(0f, -0.2f, 0f);

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

        // First scene entity
        int firstSceneEntity = sceneGraph.addSceneEntity();

        MessageComponent firstMessageComponent = NHG.entitySystem.createComponent(
                firstSceneEntity, MessageComponent.class);
        firstMessageComponent.subscribe("printNode");

        NodeComponent firstNodeComponent = NHG.entitySystem.getComponent(
                firstSceneEntity, NodeComponent.class);
        firstNodeComponent.translate(0, 0.25f, 0.07f, true);

        // Second scene entity
        int secondSceneEntity = sceneGraph.addSceneEntity(firstSceneEntity);

        MessageComponent secondMessageComponent = NHG.entitySystem.createComponent(
                secondSceneEntity, MessageComponent.class);
        secondMessageComponent.subscribe("printNode");

        NodeComponent secondNodeComponent = NHG.entitySystem.getComponent(
                secondSceneEntity, NodeComponent.class);
        secondNodeComponent.translate(0, 0.35f, 0.13f, true);

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get("asset");
                        NHG.logger.log(this, asset.alias);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            int rootSceneEntity = sceneGraph.getRootEntity();
            NodeComponent rootNodeComponent = NHG.entitySystem.getComponent(
                    rootSceneEntity, NodeComponent.class);
            rootNodeComponent.translate(0, 0.24f, 0, true);

            NHG.messaging.send(new Message("printNode"));
        }

        camera.update();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(plane);
        modelBatch.end();
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
        configurationBuilder.with(new TestSystem());
        configurationBuilder.with(new TestNodeSystem());
    }
}