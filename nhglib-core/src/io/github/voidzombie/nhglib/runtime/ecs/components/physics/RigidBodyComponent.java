package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
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
        body.dispose();
        motionState.dispose();
        collisionShape.dispose();
        constructionInfo.dispose();
    }

    public void build(btCollisionShape collisionShape, float mass) {
        build(collisionShape, Collision.WANTS_DEACTIVATION, mass);
    }

    public void build(btCollisionShape collisionShape, int activationState, float mass) {
        this.collisionShape = collisionShape;
        constructionInfo = getConstructionInfo(collisionShape, mass);

        motionState = new MotionState();
        body = new btRigidBody(constructionInfo);
        //body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setSleepingThresholds(1f / 1000f, 1f / 1000f);
    }

    public void addToWorld(btDynamicsWorld world, Matrix4 transform) {
        if (!added) {
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

    public Quaternion getRotation() {
        return motionState.transform.getRotation(new Quaternion());
    }

    public Vector3 getScale() {
        return motionState.transform.getScale(new Vector3());
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
