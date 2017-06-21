package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.WheelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
public class PhysicsSystem extends IteratingSystem implements Disposable {
    private final static float TIME_STEP = 1f / 60f;

    private boolean physicsInitialized;

    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher collisionDispatcher;
    private btDbvtBroadphase dbvtBroadphase;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;
    private ComponentMapper<VehicleComponent> vehicleMapper;
    private ComponentMapper<WheelComponent> wheelMapper;

    public PhysicsSystem() {
        super(Aspect
                .all(NodeComponent.class)
                .one(RigidBodyComponent.class, VehicleComponent.class, WheelComponent.class));

        initPhysics();
    }

    @Override
    protected void begin() {
        super.begin();

        dynamicsWorld.stepSimulation(
                Gdx.graphics.getDeltaTime(),
                5,
                TIME_STEP);
    }

    @Override
    protected void process(int entityId) {
        RigidBodyComponent bodyComponent = null;
        VehicleComponent vehicleComponent = null;
        WheelComponent wheelComponent = null;

        NodeComponent nodeComponent = nodeMapper.get(entityId);

        if (rigidBodyMapper.has(entityId)) {
            bodyComponent = rigidBodyMapper.get(entityId);
        } else if (wheelMapper.has(entityId)) {
            wheelComponent = wheelMapper.get(entityId);
        } else if (vehicleMapper.has(entityId)) {
            vehicleComponent = vehicleMapper.get(entityId);
        }

        if (bodyComponent != null) {
            processBodyComponent(bodyComponent, nodeComponent);
        } else if (wheelComponent != null) {
            processWheelComponent(wheelComponent, nodeComponent);
        } else if (vehicleComponent != null) {
            processVehicleComponent(vehicleComponent, nodeComponent);
        }
    }

    @Override
    protected void end() {
        super.end();
    }

    @Override
    public void dispose() {
        IntBag entityIds = getEntityIds();
        for (int entity : entityIds.getData()) {
            RigidBodyComponent bodyComponent = rigidBodyMapper.get(entity);

            if (bodyComponent != null) {
                bodyComponent.dispose();
            }
        }

        dynamicsWorld.dispose();
        constraintSolver.dispose();
        collisionConfiguration.dispose();
        collisionDispatcher.dispose();
        dbvtBroadphase.dispose();
    }

    public void setGravity(Vector3 gravity) {
        dynamicsWorld.setGravity(gravity);
    }

    public void setDebugDrawer(DebugDrawer debugDrawer) {
        dynamicsWorld.setDebugDrawer(debugDrawer);
    }

    public void debugDraw() {
        dynamicsWorld.debugDrawWorld();
    }

    public boolean isPhysicsInitialized() {
        return physicsInitialized;
    }

    public btDynamicsWorld getBulletWorld() {
        return dynamicsWorld;
    }

    private void initPhysics() {
        Bullet.init();

        collisionConfiguration = new btDefaultCollisionConfiguration();
        collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        dbvtBroadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();

        dynamicsWorld = new btDiscreteDynamicsWorld(collisionDispatcher, dbvtBroadphase, constraintSolver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3(0f, -1f, 0f));

        physicsInitialized = true;
    }

    private void processBodyComponent(RigidBodyComponent bodyComponent, NodeComponent nodeComponent) {
        if (!bodyComponent.isAdded()) {
            Matrix4 initialTransform = new Matrix4();

            Vector3 translation = nodeComponent.getTranslation();
            Vector3 scale = new Vector3(1, 1, 1);
            Quaternion rotationQuaternion = nodeComponent.getRotationQuaternion();

            float pitch = rotationQuaternion.getPitch();
            float yaw = rotationQuaternion.getYaw();
            float roll = rotationQuaternion.getRoll();

            float pitchRad = rotationQuaternion.getPitchRad();
            float yawRad = rotationQuaternion.getYawRad();
            float rollRad = rotationQuaternion.getRollRad();

            Matrix4 nodeTransform = new Matrix4(nodeComponent.getTransform());
            Vector3 nodeScale = nodeTransform.getScale(new Vector3());
            nodeScale.scl(-1);

            nodeTransform.scl(nodeScale);

            //initialTransform.set(translation, rotationQuaternion, scale);
            bodyComponent.addToWorld(dynamicsWorld, nodeTransform);
        } else {
            nodeComponent.setTranslation(bodyComponent.getTranslation());
            nodeComponent.setRotation(bodyComponent.getRotation());
            nodeComponent.applyTransforms();
        }
    }

    private void processVehicleComponent(VehicleComponent vehicleComponent, NodeComponent nodeComponent) {
        if (!vehicleComponent.isAdded()) {
            Matrix4 initialTransform = new Matrix4();

            Vector3 trn = nodeComponent.getTranslation();
            Vector3 scl = new Vector3(1, 1, 1);
            Quaternion rtn = nodeComponent.getRotationQuaternion();

            initialTransform.set(trn, rtn, scl);

            vehicleComponent.addToWorld(dynamicsWorld, initialTransform);
        } else {
            nodeComponent.setTranslation(vehicleComponent.getTranslation());
            nodeComponent.setRotation(vehicleComponent.getRotation());
            nodeComponent.applyTransforms();
        }
    }

    private void processWheelComponent(WheelComponent wheelComponent, NodeComponent nodeComponent) {
        float rotation = (wheelComponent.getRotation() * MathUtils.radiansToDegrees) % 360;
        float steering = wheelComponent.getSteering() * MathUtils.radiansToDegrees;

        nodeComponent.setRotation(rotation, steering, 0);
        nodeComponent.applyTransforms();
    }
}
