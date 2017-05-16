package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.threading.Threading;

/**
 * Created by worse on 04/05/2017.
 */
public class PhysicsSystem extends ThreadedIteratingSystem {
    private final static float TIME_STEP = 1f / 60f;

    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher collisionDispatcher;
    private btDbvtBroadphase dbvtBroadphase;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;

    public PhysicsSystem(Threading threading) {
        super(Aspect.all(RigidBodyComponent.class, NodeComponent.class), threading);
        initPhysics();
    }

    @Override
    protected void begin() {
        super.begin();
        dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(),
                5,
                TIME_STEP);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        RigidBodyComponent bodyComponent = rigidBodyMapper.get(entityId);

        Matrix4 bodyTransform = bodyComponent.getTransform();
        nodeComponent.setTransform(bodyTransform);
    }

    public void setGravity(Vector3 gravity) {
        dynamicsWorld.setGravity(gravity);
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
