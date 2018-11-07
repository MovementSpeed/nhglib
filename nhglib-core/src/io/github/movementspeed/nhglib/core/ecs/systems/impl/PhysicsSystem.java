package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
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
import io.github.movementspeed.nhglib.core.ecs.components.physics.RigidBodyComponent;
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent;
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.base.NhgIteratingSystem;

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
public class PhysicsSystem extends NhgIteratingSystem {
    public static float TIME_STEP = 1f / 60f;

    private boolean physicsInitialized;

    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher collisionDispatcher;
    private btDbvtBroadphase dbvtBroadphase;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;
    private ComponentMapper<VehicleComponent> vehicleMapper;
    private ComponentMapper<TyreComponent> wheelMapper;

    public PhysicsSystem() {
        super(Aspect
                .all(NodeComponent.class)
                .one(RigidBodyComponent.class, VehicleComponent.class, TyreComponent.class));

        initPhysics();
    }

    @Override
    public void dispose() {
        physicsInitialized = false;

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
        TyreComponent tyreComponent = null;

        NodeComponent nodeComponent = nodeMapper.get(entityId);

        if (rigidBodyMapper.has(entityId)) {
            bodyComponent = rigidBodyMapper.get(entityId);
        } else if (wheelMapper.has(entityId)) {
            tyreComponent = wheelMapper.get(entityId);
        } else if (vehicleMapper.has(entityId)) {
            vehicleComponent = vehicleMapper.get(entityId);
        }

        if (bodyComponent != null) {
            processBodyComponent(bodyComponent, nodeComponent);
        } else if (tyreComponent != null) {
            processWheelComponent(tyreComponent, nodeComponent);
        } else if (vehicleComponent != null) {
            processVehicleComponent(vehicleComponent, nodeComponent);
        }
    }

    @Override
    protected void end() {
        super.end();
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
        if (bodyComponent.state == RigidBodyComponent.State.READY) {
            if (!bodyComponent.isAdded()) {
                Matrix4 initialTransform = new Matrix4();

                Vector3 translation = nodeComponent.node.translation;
                Vector3 scale = new Vector3(1, 1, 1);
                Quaternion rotation = nodeComponent.node.rotation;

                initialTransform.set(translation, rotation, scale);
                bodyComponent.addToWorld(dynamicsWorld, initialTransform);
            } else {
                if (bodyComponent.kinematic) {
                    bodyComponent.setTransform(nodeComponent.getTransform());
                } else {
                    nodeComponent.setTranslation(bodyComponent.getTranslation());
                    nodeComponent.setRotation(bodyComponent.getRotation());
                    nodeComponent.applyTransforms();
                }
            }
        }
    }

    private void processVehicleComponent(VehicleComponent vehicleComponent, NodeComponent nodeComponent) {
        if (vehicleComponent.state == RigidBodyComponent.State.READY) {
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
    }

    private void processWheelComponent(TyreComponent tyreComponent, NodeComponent nodeComponent) {
        if (tyreComponent.state == TyreComponent.State.READY) {
            float rotation = (tyreComponent.getRotation() * MathUtils.radiansToDegrees) % 360;
            float steering = tyreComponent.getSteering() * MathUtils.radiansToDegrees;

            nodeComponent.setRotation(rotation, steering, 0);
            nodeComponent.applyTransforms();
        }
    }
}
