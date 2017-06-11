package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import io.github.voidzombie.nhglib.physics.MotionState;

/**
 * Created by Fausto Napoli on 03/05/2017.
 */
public class RigidBodyComponent extends Component implements Disposable {
    private boolean added;

    private btRigidBody body;
    private MotionState motionState;
    private btCollisionShape collisionShape;
    private btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    @Override
    public void dispose() {
        if (body != null) {
            body.dispose();
        }

        if (motionState != null) {
            motionState.dispose();
        }

        if (collisionShape != null) {
            collisionShape.dispose();
        }

        if (constructionInfo != null) {
            constructionInfo.dispose();
        }
    }

    public void build(btCollisionShape collisionShape, float mass) {
        build(collisionShape, mass, 0.5f, 0f);
    }

    public void build(btCollisionShape collisionShape, float mass, float friction, float restitution) {
        this.collisionShape = collisionShape;
        constructionInfo = getConstructionInfo(collisionShape, mass);

        if (constructionInfo != null) {
            motionState = new MotionState();

            body = new btRigidBody(constructionInfo);
            body.setSleepingThresholds(1f / 1000f, 1f / 1000f);
            body.setFriction(friction);
            body.setRestitution(restitution);
        }
    }

    public void addToWorld(btDynamicsWorld world, Matrix4 transform) {
        if (body != null && !body.isInWorld()) {
            setTransform(transform);
            body.setMotionState(motionState);
            world.addRigidBody(body);
            added = true;
        }
    }

    public void setTransform(Matrix4 transform) {
        motionState.transform.set(transform);
    }

    public boolean isAdded() {
        return added;
    }

    public btRigidBody getBody() {
        return body;
    }

    public Matrix4 getTransform() {
        return motionState.transform;
    }

    public Vector3 getTranslation() {
        return motionState.transform.getTranslation(new Vector3());
    }

    public Vector3 getScale() {
        return motionState.transform.getScale(new Vector3());
    }

    public Quaternion getRotation() {
        return motionState.transform.getRotation(new Quaternion());
    }

    private btRigidBody.btRigidBodyConstructionInfo getConstructionInfo(btCollisionShape shape, float mass) {
        btRigidBody.btRigidBodyConstructionInfo info = null;

        if (shape != null && mass >= 0) {
            Vector3 localInertia = new Vector3();

            if (mass > 0f) {
                shape.calculateLocalInertia(mass, localInertia);
            } else {
                localInertia.set(Vector3.Zero);
            }

            info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        }

        return info;
    }
}
