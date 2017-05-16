package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import io.github.voidzombie.nhglib.physics.MotionState;

/**
 * Created by Fausto Napoli on 03/05/2017.
 */
public class RigidBodyComponent extends PooledComponent {
    private btRigidBody body;
    private MotionState motionState;

    @Override
    protected void reset() {
        body.dispose();
        body = null;

        motionState.dispose();
    }

    public void build(btCollisionShape collisionShape, float mass) {
        build(collisionShape, Collision.WANTS_DEACTIVATION, mass);
    }

    public void build(btCollisionShape collisionShape, int activationState, float mass) {
        btRigidBody.btRigidBodyConstructionInfo constructionInfo = getConstructionInfo(collisionShape, mass);

        motionState = new MotionState();
        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);
        body.setActivationState(activationState);
    }

    public Matrix4 getTransform() {
        return motionState.transform;
    }

    private btRigidBody.btRigidBodyConstructionInfo getConstructionInfo(btCollisionShape shape, float mass) {
        Vector3 localInertia = new Vector3();

        if (mass > 0f) {
            shape.calculateLocalInertia(mass, localInertia);
        } else {
            localInertia.set(Vector3.Zero);
        }

        return new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
    }
}
