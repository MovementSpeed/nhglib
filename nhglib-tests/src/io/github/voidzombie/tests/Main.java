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
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDefaultVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo;
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
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.WheelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.PhysicsSystem;
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
    private CameraComponent cameraComponent;

    // vehicle
    btRaycastVehicle.btVehicleTuning vehicleTuning;
    btVehicleRaycaster vehicleRaycaster;
    btRaycastVehicle vehicle;

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

        Gdx.input.setCursorCatched(true);

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

                                buildVehicle();
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

        if (vehicle != null) {
            for (int i = 0; i < 4; i++) {
                //vehicle.updateWheelTransform(i);
            }
        }
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
                        vehicle.applyEngineForce(0.5f, 0);
                        vehicle.applyEngineForce(0.5f, 1);
                    } else {
                        vehicle.applyEngineForce(0, 0);
                        vehicle.applyEngineForce(0, 1);
                    }
                    break;

                case "throttleBackward":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicle.applyEngineForce(-0.5f, 0);
                        vehicle.applyEngineForce(-0.5f, 1);
                    } else {
                        vehicle.applyEngineForce(0, 0);
                        vehicle.applyEngineForce(0, 1);
                    }
                    break;

                case "steerLeft":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicle.setSteeringValue(25 * MathUtils.degreesToRadians, 0);
                        vehicle.setSteeringValue(25 * MathUtils.degreesToRadians, 1);
                    } else {
                        vehicle.setSteeringValue(0, 0);
                        vehicle.setSteeringValue(0, 1);
                    }
                    break;

                case "steerRight":
                    if (input.getInputAction() == InputAction.DOWN) {
                        vehicle.setSteeringValue(-25 * MathUtils.degreesToRadians, 0);
                        vehicle.setSteeringValue(-25 * MathUtils.degreesToRadians, 1);
                    } else {
                        vehicle.setSteeringValue(0, 0);
                        vehicle.setSteeringValue(0, 1);
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

    private void buildVehicle() {
        wheels = new int[4];
        PhysicsSystem physicsSystem = nhg.entities.getEntitySystem(PhysicsSystem.class);

        // Create the car chassis entity
        vehicleChassis = scene.sceneGraph.createSceneEntity("vehicle_chassis");
        scene.sceneGraph.addSceneEntity(vehicleChassis);

        // Create entities for all the wheels
        for (int i = 0; i < 4; i++) {
            wheels[i] = scene.sceneGraph.createSceneEntity("vehicle_wheel_" + i);
            scene.sceneGraph.addSceneEntity(wheels[i]);
        }

        // Create a model component for the chassis
        nhg.assets.loadAsset(new Asset("car", "models/car.obj", Model.class));
        Model car = nhg.assets.get("car");

        ModelComponent modelComponent = nhg.entities.createComponent(vehicleChassis, ModelComponent.class);
        modelComponent.initWithModel(car);

        // Create a model component for the chassis
        nhg.assets.loadAsset(new Asset("wheel", "models/wheel.obj", Model.class));
        Model wheel = nhg.assets.get("wheel");

        // Create a rigid body component for the chassis
        RigidBodyComponent chassisBody = nhg.entities.createComponent(vehicleChassis, RigidBodyComponent.class);

        BoundingBox boundingBox = new BoundingBox();
        Vector3 chassisHalfExtents = car.calculateBoundingBox(boundingBox).getDimensions(new Vector3()).scl(0.5f);
        Vector3 wheelHalfExtents = wheel.calculateBoundingBox(boundingBox).getDimensions(new Vector3()).scl(0.5f);

        btBoxShape boxShape = new btBoxShape(chassisHalfExtents);
        chassisBody.build(boxShape, 0, 1f, 0.1f, 0f);

        // Create a rigid body component for every wheel
        for (int i = 0; i < 4; i++) {
            ModelComponent wheelModel = nhg.entities.createComponent(wheels[i], ModelComponent.class);
            wheelModel.initWithModel(wheel);
        }

        // Create the physics vehicle
        vehicleRaycaster = new btDefaultVehicleRaycaster(physicsSystem.getBulletWorld());
        vehicleTuning = new btRaycastVehicle.btVehicleTuning();
        vehicle = new btRaycastVehicle(vehicleTuning, chassisBody.getBody(), vehicleRaycaster);

        chassisBody.getBody().setActivationState(Collision.DISABLE_DEACTIVATION);
        physicsSystem.getBulletWorld().addVehicle(vehicle);

        vehicle.setCoordinateSystem(0, 1, 2);

        Vector3 point = new Vector3();
        Vector3 direction = new Vector3(0, -1, 0);
        Vector3 axis = new Vector3(-1, 0, 0);

        btWheelInfo wheelInfo0 = vehicle.addWheel(point.set(chassisHalfExtents).scl(0.9f, -0.8f, 0.7f),
                direction, axis, wheelHalfExtents.z * 0.3f, wheelHalfExtents.z, vehicleTuning,
                true);

        btWheelInfo wheelInfo1 = vehicle.addWheel(point.set(chassisHalfExtents).scl(-0.9f, -0.8f, 0.7f),
                direction, axis, wheelHalfExtents.z * 0.3f, wheelHalfExtents.z, vehicleTuning,
                true);

        btWheelInfo wheelInfo2 = vehicle.addWheel(point.set(chassisHalfExtents).scl(0.9f, -0.8f, -0.5f),
                direction, axis, wheelHalfExtents.z * 0.3f, wheelHalfExtents.z, vehicleTuning,
                false);

        btWheelInfo wheelInfo3 = vehicle.addWheel(point.set(chassisHalfExtents).scl(-0.9f, -0.8f, -0.5f),
                direction, axis, wheelHalfExtents.z * 0.3f, wheelHalfExtents.z, vehicleTuning,
                false);

        WheelComponent wheelComponent0 = nhg.entities.createComponent(wheels[0], WheelComponent.class);
        wheelComponent0.build(wheelInfo0, vehicle, 0);

        WheelComponent wheelComponent1 = nhg.entities.createComponent(wheels[1], WheelComponent.class);
        wheelComponent1.build(wheelInfo1, vehicle, 1);

        WheelComponent wheelComponent2 = nhg.entities.createComponent(wheels[2], WheelComponent.class);
        wheelComponent2.build(wheelInfo2, vehicle, 2);

        WheelComponent wheelComponent3 = nhg.entities.createComponent(wheels[3], WheelComponent.class);
        wheelComponent3.build(wheelInfo3, vehicle, 3);
    }
}