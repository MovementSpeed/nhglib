package io.github.movementspeed.tests;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.files.HDRData;
import io.github.movementspeed.nhglib.graphics.lights.LightProbe;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.particles.ParticleShader;
import io.github.movementspeed.nhglib.graphics.worlds.NhgWorld;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.NhgInput;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.runtime.entry.NhgEntry;
import io.github.movementspeed.nhglib.runtime.messaging.Message;
import io.github.movementspeed.nhglib.utils.data.Bounds;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.github.movementspeed.tests.systems.TestNodeSystem;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NhgEntry implements InputListener {
    private Scene scene;
    private NhgWorld world;
    private NodeComponent cameraNode;
    private CameraComponent cameraComponent;
    private RenderingSystem renderingSystem;
    private Environment environment;

    private LightProbe lightProbe;
    private Mesh cubeMesh;

    private Stage stage;
    private Table table;
    private Skin skin;

    @Override
    public void onStart() {
        super.onStart();
        Nhg.debugLogs = true;
        Nhg.debugDrawPhysics = false;
        ParticleShader.softParticles = false;

        ModelBuilder mb = new ModelBuilder();
        Model cube = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        cubeMesh = cube.meshes.first();

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        table = new Table(skin);
        table.setFillParent(true);

        stage = new Stage();
        stage.addActor(table);

        table.setDebug(true);

        final TextButton button = new TextButton("Jump", skin);
        table.add(button);
        table.align(Align.topLeft);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                button.setText("x: " + x + " y: " + y);
            }
        });
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
                new DefaultWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        nhg.input.addInputProcessor(stage);
        nhg.input.addListener(this);

        nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        renderingSystem = nhg.entities.getEntitySystem(RenderingSystem.class);
        renderingSystem.setClearColor(Color.GRAY);
        renderingSystem.setRenderScale(1.0f);

        environment = renderingSystem.getEnvironment();

        NhgLightsAttribute lightsAttribute = new NhgLightsAttribute();

        for (int i = 0; i < 10; i++) {
            NhgLight light = NhgLight.point(10, 10, new Color(Color.rgba8888(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 1f)));
            light.position.set(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f));
            lightsAttribute.lights.add(light);
        }

        GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute();
        gammaCorrectionAttribute.gammaCorrection = true;

        environment.set(lightsAttribute);
        environment.set(gammaCorrectionAttribute);

        // Subscribe to asset events
        nhg.messaging.get(Strings.Events.assetLoaded, Strings.Events.assetLoadingFinished, Strings.Events.sceneLoaded)
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

                                cameraComponent = nhg.entities.getComponent(cameraEntity, CameraComponent.class);
                            } else if (asset.is("inputMap")) {
                                nhg.input.fromJson((JsonValue) nhg.assets.get(asset));
                                nhg.input.setActiveContext("game", true);
                                nhg.input.setActiveContext("global", true);
                            }
                        } else if (message.is(Strings.Events.sceneLoaded)) {
                            NhgLogger.log(this, "Scene loaded");

                            HDRData data = nhg.assets.get("newport_loft");

                            lightProbe = new LightProbe();
                            lightProbe.build(data,
                                    128f, 128f,
                                    32f, 32f,
                                    64f, 64f,
                                    128f, 128f);

                            IBLAttribute irradianceAttribute = IBLAttribute.createIrradiance(lightProbe.getIrradiance());
                            IBLAttribute prefilterAttribute = IBLAttribute.createPrefilter(lightProbe.getPrefilter());
                            IBLAttribute brdfAttribute = IBLAttribute.createBrdf(lightProbe.getBrdf());

                            environment.set(irradianceAttribute);
                            environment.set(prefilterAttribute);
                            environment.set(brdfAttribute);
                        }
                    }
                });
    }

    @Override
    public void onUpdate(float delta) {
        super.onUpdate(delta);
        world.update();
        /*stage.act(delta);
        stage.draw();*/
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void onDispose() {
        super.onDispose();
        stage.dispose();
        skin.dispose();
    }

    @Override
    public Array<BaseSystem> onConfigureEntitySystems() {
        Array<BaseSystem> systems = new Array<>();
        systems.add(new TestNodeSystem());

        return systems;
    }

    @Override
    public void onKeyInput(NhgInput input) {
        if (scene != null) {
            NodeComponent nodeComponent = cameraNode;

            switch (input.getName()) {
                case "strafeRight":
                    nodeComponent.translate(0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "strafeLeft":
                    nodeComponent.translate(-0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "forward":
                    nodeComponent.translate(0, 0, -0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "backward":
                    nodeComponent.translate(0, 0, 0.5f * Gdx.graphics.getDeltaTime());
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
            NodeComponent nodeComponent = cameraNode;

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

                case "throw":
                    NhgLogger.log(this, "Throw!");
                    break;

                case "jump":
                    NhgLogger.log(this, "Jump!");
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

        if (!input.getName().contentEquals("mouseLook")) {
            switch (input.getName()) {
                case "throw":
                    NhgLogger.log(this, "Throw!");
                    break;

                case "jump":
                    NhgLogger.log(this, "Jump!");
                    break;
            }
        }
    }
}