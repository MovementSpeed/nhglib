package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.voidzombie.nhglib.graphics.worlds.NhgWorld;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.impl.LargeWorldStrategy;
import io.github.voidzombie.nhglib.input.interfaces.InputListener;
import io.github.voidzombie.nhglib.input.models.NhgInput;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bounds;
import io.github.voidzombie.nhglib.utils.data.Strings;
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

        world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
                new LargeWorldStrategy(nhg.entities),
                new Bounds(2f, 2f, 2f));

        fpsLogger = new FPSLogger();
        renderer20 = new ImmediateModeRenderer20(false, true, 0);

        nhg.input.addListener(this);

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.MipMap;
        param.magFilter = Texture.TextureFilter.Linear;
        param.genMipMaps = true;

        nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        GraphicsSystem graphicsSystem = nhg.entities.getEntitySystem(GraphicsSystem.class);
        graphicsSystem.setClearColor(Color.BLACK);

        Environment environment = graphicsSystem.getEnvironment();

        GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute();
        gammaCorrectionAttribute.gammaCorrection = true;

        //environment.set(lightsAttribute);
        environment.set(gammaCorrectionAttribute);

        // Subscribe to asset events
        nhg.messaging.get(Strings.Events.assetLoaded, Strings.Events.assetLoadingFinished)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Strings.Events.assetLoaded)) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (asset.is("scene")) {
                                scene = nhg.assets.get(asset);

                                world.loadScene(scene);
                                world.setReferenceEntity("camera");

                                Integer cameraEntity = scene.sceneGraph.getSceneEntity("camera");
                                cameraNode = nhg.entities.getComponent(
                                        cameraEntity, NodeComponent.class);
                            } else if (asset.is("inputMap")) {
                                nhg.input.fromJson((JsonValue) nhg.assets.get(asset));
                                nhg.input.setActiveContext("game", true);
                                nhg.input.setActiveContext("global", true);
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
            NodeComponent nodeComponent = cameraNode;

            switch (input.getName()) {
                case "strafeRight":
                    nodeComponent.translate(0.1f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "strafeLeft":
                    nodeComponent.translate(-0.1f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "forward":
                    nodeComponent.translate(0, 0, -0.1f * Gdx.graphics.getDeltaTime());
                    break;

                case "backward":
                    nodeComponent.translate(0, 0, 0.1f * Gdx.graphics.getDeltaTime());
                    break;

                case "jump":
                    cameraNode.rotate(0, 0, 0.1f);
                    break;

                case "sprint":
                    cameraNode.rotate(0, 0, -0.1f);
                    break;

                case "exit":
                    nhg.messaging.send(new Message(Strings.Events.engineDestroy));
                    break;
            }

            nodeComponent.applyTransforms();
        }
    }

    @Override
    public void onStickInput(NhgInput input) {
        if (scene != null) {
            int entity = scene.sceneGraph.getSceneEntity("weaponEntity1");
            NodeComponent nodeComponent = nhg.entities.getComponent(
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