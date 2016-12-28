package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.worlds.impl.DefaultWorld;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
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
    private DefaultWorld world;
    private FPSLogger fpsLogger;
    private GraphicsSystem graphicsSystem;
    private ImmediateModeRenderer20 renderer20;

    @Override
    public void engineStarted() {
        super.engineStarted();
        NHG.debugLogs = true;

        world = new DefaultWorld();
        fpsLogger = new FPSLogger();
        renderer20 = new ImmediateModeRenderer20(false, true, 0);

        NHG.assets.queueAsset(new Asset("scene", "myscene0.nhs", Scene.class));

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);

                        if (asset.is("scene")) {
                            scene = NHG.assets.get(asset);

                            world.addScene(scene);
                        }
                    }
                });

        graphicsSystem = NHG.entitySystem.getEntitySystem(GraphicsSystem.class);
        graphicsSystem.camera.position.set(0, 0, 2f);
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();
    }

    @Override
    public void engineUpdate(float delta) {
        super.engineUpdate(delta);
        //fpsLogger.log();

        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("root");
            NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                    entity, NodeComponent.class);

            boolean input = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                nodeComponent.translate(0, 0, 100f);
                graphicsSystem.camera.position.add(0, 0, 100f);

                NHG.logger.log(this, "%s", graphicsSystem.camera.position);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                nodeComponent.translate(0, 0, -100f);
                graphicsSystem.camera.position.add(0, 0, -100f);

                NHG.logger.log(this, "%s", graphicsSystem.camera.position);
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
                graphicsSystem.camera.rotate(1, 0, 1, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                nodeComponent.rotate(0, 10f * delta, 0);
                graphicsSystem.camera.rotate(-1, 0, 1, 0);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            world.unloadScene("scene0");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            world.loadScene("scene0");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            NHG.messaging.send(new Message(NHG.strings.events.engineDestroy));
        }

        renderer20.begin(graphicsSystem.camera.combined, GL20.GL_LINES);

        cube(0, 0, 0, 1, 1, 1, 0, 1, 0, 1);
        cube(1.01f, 0, 0, 1, 1, 1, 1, 0, 0, 1);
        cube(-1.01f, 0, 0, 1, 1, 1, 1, 0, 0, 1);
        cube(0, 0, 1.01f, 1, 1, 1, 1, 0, 0, 1);
        cube(0, 0, -1.01f, 1, 1, 1, 1, 0, 0, 1);

        renderer20.end();
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
    }

    public void line(float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r, float g, float b, float a) {
        renderer20.color(r, g, b, a);
        renderer20.vertex(x1, y1, z1);
        renderer20.color(r, g, b, a);
        renderer20.vertex(x2, y2, z2);
    }

    public void cube(float x, float y, float z, float width, float height, float depth, float r, float g, float b, float a) {
        // top face
        line(x + width / 2, y + height / 2, z - depth / 2,
                x - width / 2, y + height / 2, z - depth / 2,
                r, g, b, a);

        line(x + width / 2, y + height / 2, z + depth / 2,
                x - width / 2, y + height / 2, z + depth / 2,
                r, g, b, a);

        // bottom face
        line(x + width / 2, y - height / 2, z - depth / 2,
                x - width / 2, y - height / 2, z - depth / 2,
                r, g, b, a);

        line(x + width / 2, y - height / 2, z + depth / 2,
                x - width / 2, y - height / 2, z + depth / 2,
                r, g, b, a);

        // left face
        line(x - width / 2, y - height / 2, z - depth / 2,
                x - width / 2, y - height / 2, z + depth / 2,
                r, g, b, a);

        line(x - width / 2, y + height / 2, z - depth / 2,
                x - width / 2, y + height / 2, z + depth / 2,
                r, g, b, a);

        line(x - width / 2, y - height / 2, z - depth / 2,
                x - width / 2, y + height / 2, z - depth / 2,
                r, g, b, a);

        line(x - width / 2, y - height / 2, z + depth / 2,
                x - width / 2, y + height / 2, z + depth / 2,
                r, g, b, a);

        // right face
        line(x + width / 2, y - height / 2, z - depth / 2,
                x + width / 2, y - height / 2, z + depth / 2,
                r, g, b, a);

        line(x + width / 2, y + height / 2, z - depth / 2,
                x + width / 2, y + height / 2, z + depth / 2,
                r, g, b, a);

        line(x + width / 2, y - height / 2, z - depth / 2,
                x + width / 2, y + height / 2, z - depth / 2,
                r, g, b, a);

        line(x + width / 2, y - height / 2, z + depth / 2,
                x + width / 2, y + height / 2, z + depth / 2,
                r, g, b, a);
    }
}