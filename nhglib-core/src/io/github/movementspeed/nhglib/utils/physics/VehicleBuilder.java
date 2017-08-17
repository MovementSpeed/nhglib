package io.github.movementspeed.nhglib.utils.physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.physics.WheelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.PhysicsSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class VehicleBuilder {
    private int builtWheels;
    private int vehicleEntity;

    private PhysicsSystem physicsSystem;
    private SceneGraph sceneGraph;
    private Entities entities;
    private Assets assets;

    private Model chassisModel;
    private Model wheelModel;

    private VehicleComponent vehicleComponent;

    private int wheelEntities[];

    public VehicleBuilder(Entities entities, Assets assets, Scene scene) {
        this.entities = entities;
        this.assets = assets;
        this.sceneGraph = scene.sceneGraph;
        this.physicsSystem = entities.getEntitySystem(PhysicsSystem.class);
    }

    public VehicleBuilder begin(int entity, int wheels) {
        wheelEntities = new int[wheels];
        vehicleEntity = entity;

        for (int i = 0; i < wheels; i++) {
            wheelEntities[i] = sceneGraph.addSceneEntity(entity + "_wheel_" + i);
        }

        return this;
    }

    public VehicleBuilder begin(String name, int wheels) {
        wheelEntities = new int[wheels];

        // Create the main vehicle entity
        vehicleEntity = sceneGraph.addSceneEntity(name + "_chassis");

        // Create wheel entities
        for (int i = 0; i < wheels; i++) {
            wheelEntities[i] = sceneGraph.addSceneEntity(name + "_wheel_" + i);
        }

        return this;
    }

    public VehicleBuilder setChassisAsset(Asset chassisAsset) {
        assets.loadAsset(chassisAsset, new Assets.AssetListener() {
            @Override
            public void onAssetLoaded(Asset asset) {
                setChassisModel((Model) assets.get(asset));
            }
        });

        return this;
    }

    public VehicleBuilder setWheelAsset(Asset wheelAsset) {
        assets.loadAsset(wheelAsset, new Assets.AssetListener() {
            @Override
            public void onAssetLoaded(Asset asset) {
                setWheelModel((Model) assets.get(asset));
            }
        });

        return this;
    }

    public VehicleBuilder setChassisModel(Model model) {
        this.chassisModel = model;

        ModelComponent modelComponent = entities.createComponent(vehicleEntity, ModelComponent.class);
        modelComponent.initWithModel(model);

        return this;
    }

    public VehicleBuilder setWheelModel(Model model) {
        this.wheelModel = model;

        for (int i = 0; i < wheelEntities.length; i++) {
            ModelComponent wheelModel = entities.createComponent(wheelEntities[i], ModelComponent.class);
            wheelModel.initWithModel(model);
        }

        return this;
    }

    public VehicleBuilder buildChassis(float mass) {
        Vector3 chassisHalfExtents = chassisModel
                .calculateBoundingBox(new BoundingBox())
                .getDimensions(new Vector3())
                .scl(0.5f);

        btBoxShape boxShape = new btBoxShape(chassisHalfExtents);
        return buildChassis(boxShape, mass);
    }

    public VehicleBuilder buildChassis(btCollisionShape vehicleShape, float mass) {
        vehicleComponent = entities.createComponent(vehicleEntity, VehicleComponent.class);
        vehicleComponent.build(physicsSystem.getBulletWorld(), vehicleShape, mass);

        return this;
    }

    public VehicleBuilder buildWheel(Vector3 point, Vector3 direction, Vector3 axis, float friction,
                                     boolean frontWheel) {
        Vector3 wheelHalfExtents = wheelModel
                .calculateBoundingBox(new BoundingBox())
                .getDimensions(new Vector3())
                .scl(0.5f);

        return buildWheel(point, direction, axis, wheelHalfExtents.z, wheelHalfExtents.z * 0.3f,
                friction, frontWheel);
    }

    public VehicleBuilder buildWheel(Vector3 point, Vector3 direction, Vector3 axis, float radius,
                                     float suspensionRestLength, float friction, boolean frontWheel) {
        if (builtWheels < wheelEntities.length) {
            vehicleComponent.addWheel(point, direction, axis, radius, suspensionRestLength, friction, frontWheel);

            WheelComponent wheelComponent = entities.createComponent(wheelEntities[builtWheels], WheelComponent.class);
            wheelComponent.build();
            wheelComponent.wheelIndex = builtWheels;

            builtWheels++;
        }

        return this;
    }

    public VehicleComponent end() {
        return vehicleComponent;
    }
}
