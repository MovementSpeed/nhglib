package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.worlds.NHGWorld;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.impl.LargeWorldStrategy;
import io.github.voidzombie.nhglib.input.*;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.entry.NHGEntry;
import io.github.voidzombie.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NHGEntry implements InputListener {
    private Scene scene;
    private NHGWorld world;
    private FPSLogger fpsLogger;
    private GraphicsSystem graphicsSystem;
    private ImmediateModeRenderer20 renderer20;

    @Override
    public void engineStarted() {
        super.engineStarted();
        NHG.debugLogs = true;

        world = new NHGWorld(
                new LargeWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        fpsLogger = new FPSLogger();
        renderer20 = new ImmediateModeRenderer20(false, true, 0);

        NHG.input.addListener(this);

        NHG.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        NHG.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        // Subscribe to asset events
        NHG.messaging.get(NHG.strings.events.assetLoaded, NHG.strings.events.assetLoadingFinished)
                .subscribe(message -> {
                    if (message.is(NHG.strings.events.assetLoaded)) {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);

                        if (asset.is("scene")) {
                            scene = NHG.assets.get(asset);
                            world.addScene(scene);
                            world.loadScene("scene0");
                            world.setReferenceEntity("weaponEntity1");
                        } else if (asset.is("inputMap")) {
                            NHG.input.fromJson(NHG.assets.get(asset));
                            NHG.input.setActive("game", true);
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
        world.update();

        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                    entity, NodeComponent.class);

            boolean input = false;

            /*if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                nodeComponent.translate(0, 0, 0.5f * delta);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                nodeComponent.translate(0, 0, -0.5f * delta);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                nodeComponent.translate(-0.5f * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                nodeComponent.translate(0.5f * delta, 0, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.T)) {
                nodeComponent.translate(0, -0.5f * delta, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.G)) {
                nodeComponent.translate(0, 0.5f * delta, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                graphicsSystem.camera.rotate(1, 0, 1, 0);
                input = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
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
            }*/

            if (input) {

            }
        }

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            NHG.messaging.send(new Message(NHG.strings.events.engineDestroy));
        }*/

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

    @Override
    public void onInput(NHGInput input) {
        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                    entity, NodeComponent.class);

            switch (input.getName()) {
                case "backward":
                    nodeComponent.translate(0, 0, 0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "forward":
                    nodeComponent.translate(0, 0, -0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "strafeLeft":
                    nodeComponent.translate(-0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "strafeRight":
                    nodeComponent.translate(0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "jump":
                    break;

                case "sprint":
                    break;
            }

            nodeComponent.applyTransforms();
        }
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