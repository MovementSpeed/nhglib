package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
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
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
public class PhysicsSystem extends IteratingSystem implements Disposable {
    private final static float TIME_STEP = 1f / 60f;

    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher collisionDispatcher;
    private btDbvtBroadphase dbvtBroadphase;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;

    public PhysicsSystem() {
        super(Aspect.all(RigidBodyComponent.class, NodeComponent.class));
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
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        RigidBodyComponent bodyComponent = rigidBodyMapper.get(entityId);

        if (!bodyComponent.isAdded()) {
            Matrix4 initialTransform = new Matrix4();

            Vector3 trn = nodeComponent.getTranslation();
            Vector3 scl = new Vector3(1, 1, 1);
            Quaternion rtn = nodeComponent.getRotationQuaternion();

            initialTransform.set(trn, rtn, scl);

            bodyComponent.addToWorld(dynamicsWorld, initialTransform);
        } else {
            nodeComponent.setTranslation(bodyComponent.getTranslation());
            nodeComponent.setRotation(bodyComponent.getRotation());
            nodeComponent.applyTransforms();
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

    private void initPhysics() {
        Bullet.init();
        collisionConfiguration = new btDefaultCollisionConfiguration();
        collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        dbvtBroadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();

        dynamicsWorld = new btDiscreteDynamicsWorld(collisionDispatcher, dbvtBroadphase, constraintSolver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3(0f, -1f, 0f));
    }
}
