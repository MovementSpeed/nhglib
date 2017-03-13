package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.lights.IntensityDirectionalLight;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.worlds.NhgWorld;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.impl.LargeWorldStrategy;
import io.github.voidzombie.nhglib.input.interfaces.InputListener;
import io.github.voidzombie.nhglib.input.models.NhgInput;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bounds;
import io.github.voidzombie.tests.systems.TestNodeSystem;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NhgEntry implements InputListener {
    private Scene scene;
    private NhgWorld world;
    private FPSLogger fpsLogger;
    private ImmediateModeRenderer20 renderer20;
    private NodeComponent cameraNode;

    @Override
    public void engineStarted() {
        super.engineStarted();
        Nhg.debugLogs = true;

        world = new NhgWorld(
                new LargeWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        fpsLogger = new FPSLogger();
        renderer20 = new ImmediateModeRenderer20(false, true, 0);

        Nhg.input.addListener(this);

        Nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        Nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        Environment environment = Nhg.entitySystem.getEntitySystem(GraphicsSystem.class).getEnvironment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, new Color(0.2666f, 0.3607f, 0.4117f, 1.0f)));
        environment.add(new IntensityDirectionalLight().set(new Color(0.9294f, 0.9764f, 1.0f, 1.0f), new Vector3(-1, -1, -1), 1.0f));

        // Subscribe to asset events
        Nhg.messaging.get(Nhg.strings.events.assetLoaded, Nhg.strings.events.assetLoadingFinished)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Nhg.strings.events.assetLoaded)) {
                            Asset asset = (Asset) message.data.get(Nhg.strings.defaults.assetKey);

                            if (asset.is("scene")) {
                                scene = Nhg.assets.get(asset);

                                world.loadScene(scene);
                                world.setReferenceEntity("weaponEntity1");

                                Integer cameraEntity = scene.sceneGraph.getSceneEntity("camera");
                                cameraNode = Nhg.entitySystem.getComponent(
                                        cameraEntity, NodeComponent.class);
                            } else if (asset.is("inputMap")) {
                                Nhg.input.fromJson((JsonValue) Nhg.assets.get(asset));
                                Nhg.input.setActiveContext("game", true);
                                Nhg.input.setActiveContext("global", true);
                            }
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
        world.update();
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
        configurationBuilder.with(new TestNodeSystem());
    }

    @Override
    public void onKeyInput(NhgInput input) {
        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = Nhg.entitySystem.getComponent(
                    entity, NodeComponent.class);

            switch (input.getName()) {
                case "backward":
                    nodeComponent.translate(0, 0, 0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "forward":
                    nodeComponent.translate(0, 0, -0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "strafeLeft":
                    nodeComponent.rotate(0, -10 * Gdx.graphics.getDeltaTime(), 0);
                    break;

                case "strafeRight":
                    nodeComponent.rotate(0, 10 * Gdx.graphics.getDeltaTime(), 0);
                    break;

                case "jump":
                    cameraNode.rotate(0, 0, 0.1f);
                    break;

                case "sprint":
                    cameraNode.rotate(0, 0, -0.1f);
                    break;

                case "exit":
                    Nhg.messaging.send(new Message(Nhg.strings.events.engineDestroy));
                    break;
            }

            nodeComponent.applyTransforms();
        }
    }

    @Override
    public void onStickInput(NhgInput input) {
        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = Nhg.entitySystem.getComponent(
                    entity, NodeComponent.class);

            Vector2 stickVector = (Vector2) input.getInputSource().getValue();

            if (stickVector != null) {
                switch (input.getName()) {
                    case "movementStick":
                        nodeComponent.translate(
                                stickVector.x * Gdx.graphics.getDeltaTime(),
                                0,
                                stickVector.y * Gdx.graphics.getDeltaTime(),
                                true);
                        break;

                    case "rotationStick":
                        nodeComponent.rotate(
                                stickVector.y * Gdx.graphics.getDeltaTime(),
                                stickVector.x * Gdx.graphics.getDeltaTime(),
                                0,
                                true
                        );
                        break;
                }
            }
        }
    }

    @Override
    public void onPointerInput(NhgInput input) {
        Vector2 pointerVector = (Vector2) input.getInputSource().getValue();

        if (pointerVector != null) {
            switch (input.getName()) {
                case "look":
                    float horizontalAxis = pointerVector.x;
                    float verticalAxis = pointerVector.y;

                    cameraNode.rotate(verticalAxis, horizontalAxis, 0);
                    cameraNode.applyTransforms();
                    break;
            }
        }
    }

    @Override
    public void onMouseInput(NhgInput input) {
        Vector2 pointerVector = (Vector2) input.getInputSource().getValue();

        if (pointerVector != null) {
            float horizontalAxis = pointerVector.x;
            float verticalAxis = pointerVector.y;

            cameraNode.rotate(verticalAxis, horizontalAxis, 0);
            cameraNode.applyTransforms();
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