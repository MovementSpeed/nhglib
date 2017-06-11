package io.github.voidzombie.tests;

import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.voidzombie.nhglib.graphics.worlds.NhgWorld;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.voidzombie.nhglib.input.enums.InputAction;
import io.github.voidzombie.nhglib.input.interfaces.InputListener;
import io.github.voidzombie.nhglib.input.models.NhgInput;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bounds;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.physics.VehicleBuilder;
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
    private CameraComponent cameraComponent;
    private VehicleComponent vehicleComponent;

    int vehicleChassis;
    int wheels[];

    float maxForce = 100f;
    float currentForce = 0f;
    float acceleration = 50f;
    float maxAngle = 60f;
    float currentAngle = 0f;
    float steerSpeed = 45f;

    @Override
    public void engineStarted() {
        super.engineStarted();
        Nhg.debugLogs = true;
        Nhg.debugDrawPhysics = true;

        //Gdx.input.setCursorCatched(true);

        world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
                new DefaultWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        fpsLogger = new FPSLogger();
        renderer20 = new ImmediateModeRenderer20(false, true, 0);

        nhg.input.addListener(this);

        nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        GraphicsSystem graphicsSystem = nhg.entities.getEntitySystem(GraphicsSystem.class);
        graphicsSystem.setClearColor(Color.GRAY);

        Environment environment = graphicsSystem.getEnvironment();

        GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute();
        gammaCorrectionAttribute.gammaCorrection = true;

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

                                ModelBuilder mb = new ModelBuilder();
                                Model planeModel = mb.createBox(2f, 0.01f, 20f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

                                int plane = scene.sceneGraph.createSceneEntity("plane");
                                scene.sceneGraph.addSceneEntity(plane);

                                ModelComponent modelComponent = nhg.entities.createComponent(plane, ModelComponent.class);
                                modelComponent.initWithModel(planeModel);

                                NodeComponent nodeComponent = nhg.entities.getComponent(plane, NodeComponent.class);
                                nodeComponent.setTranslation(0, -1f, 0, true);

                                Integer cameraEntity = scene.sceneGraph.getSceneEntity("camera");
                                cameraNode = nhg.entities.getComponent(
                                        cameraEntity, NodeComponent.class);

                                cameraComponent = nhg.entities.getComponent(cameraEntity, CameraComponent.class);

                                Integer vehicleEntity = scene.sceneGraph.getSceneEntity("kart");
                                vehicleComponent = nhg.entities.getComponent(vehicleEntity, VehicleComponent.class);
                                //ibuildVehicleSimple();
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

                case "throttleForward":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicleComponent.applyEngineForce(24, 2);
                        vehicleComponent.applyEngineForce(24, 3);
                    } else {
                        vehicleComponent.applyEngineForce(0, 2);
                        vehicleComponent.applyEngineForce(0, 3);
                    }
                    break;

                case "throttleBackward":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicleComponent.applyEngineForce(-24, 2);
                        vehicleComponent.applyEngineForce(-24, 3);
                    } else {
                        vehicleComponent.applyEngineForce(0, 2);
                        vehicleComponent.applyEngineForce(0, 3);
                    }
                    break;

                case "brake":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicleComponent.setBrake(0.03f, 0);
                        vehicleComponent.setBrake(0.03f, 1);
                    } else {
                        vehicleComponent.setBrake(0, 0);
                        vehicleComponent.setBrake(0, 1);
                    }
                    break;

                case "steerLeft":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicleComponent.setSteeringValue(30 * MathUtils.degreesToRadians, 0);
                        vehicleComponent.setSteeringValue(30 * MathUtils.degreesToRadians, 1);
                    } else {
                        vehicleComponent.setSteeringValue(0, 0);
                        vehicleComponent.setSteeringValue(0, 1);
                    }
                    break;

                case "steerRight":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicleComponent.setSteeringValue(-30 * MathUtils.degreesToRadians, 0);
                        vehicleComponent.setSteeringValue(-30 * MathUtils.degreesToRadians, 1);
                    } else {
                        vehicleComponent.setSteeringValue(0, 0);
                        vehicleComponent.setSteeringValue(0, 1);
                    }
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

    private void buildVehicleSimple() {
        Vector3 point = new Vector3();
        Vector3 direction = new Vector3(0, -1, 0);
        Vector3 axis = new Vector3(-1, 0, 0);

        Vector3 chassisHalfExtents = new Vector3(0.876f, 0.267f, 1.219f).scl(0.5f);
        btBoxShape carShape = new btBoxShape(chassisHalfExtents);

        VehicleBuilder builder = new VehicleBuilder(nhg.entities, nhg.assets, scene);

        vehicleComponent = builder.begin("kart", 4)
                .setChassisAsset(new Asset("kartAsset", "models/gk_chassis.g3db", Model.class))
                .setWheelAsset(new Asset("wheel", "models/gk_wheel.g3db", Model.class))
                .buildChassis(carShape, 5f)
                .buildWheel(point.set(0.313f, 0, 0.334f), direction, axis, 20, true)
                .buildWheel(point.set(-0.313f, 0, 0.334f), direction, axis, 20, true)
                .buildWheel(point.set(0.313f, 0, -0.386f), direction, axis, 15, false)
                .buildWheel(point.set(-0.313f, 0, -0.386f), direction, axis, 15, false)
                .end();
    }
}