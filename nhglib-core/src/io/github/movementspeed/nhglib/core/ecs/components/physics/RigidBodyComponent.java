package io.github.movementspeed.nhglib.core.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.physics.MotionState;
import io.github.movementspeed.nhglib.physics.models.*;

/**
 * Created by Fausto Napoli on 03/05/2017.
 */
public class RigidBodyComponent extends Component implements Disposable {
    public boolean added;
    public boolean collisionFiltering;
    public boolean kinematic;

    public short group;
    public short mask;

    public float mass;
    public float friction;
    public float restitution;

    public State state;

    public btRigidBody body;
    public MotionState motionState;
    public RigidBodyShape rigidBodyShape;
    public btCollisionShape collisionShape;
    public btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    private Vector3 translation;
    private Vector3 scale;
    private Quaternion rotation;
    private Matrix4 initialTransform;

    public RigidBodyComponent() {
        state = State.NOT_INITIALIZED;

        translation = new Vector3();
        scale = new Vector3();
        rotation = new Quaternion();
        initialTransform = new Matrix4();
    }

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
        build(collisionShape, mass, 0.5f, 0f, (short) -1, new short[]{});
    }

    public void build(btCollisionShape collisionShape, float mass, float friction, float restitution, short group, short[] masks) {
        this.collisionShape = collisionShape;
        buildBody(mass, friction, restitution);
    }

    public void build(Assets assets) {
        buildCollisionShape(assets);
        buildBody(mass, friction, restitution);
    }

    public void addToWorld(btDynamicsWorld world, Matrix4 transform) {
        if (body != null && !body.isInWorld()) {
            initialTransform.set(transform);
            setTransform(transform);
            body.setMotionState(motionState);

            if (collisionFiltering) {
                world.addRigidBody(body, group, mask);
            } else {
                world.addRigidBody(body);
            }

            added = true;
        }
    }

    public void setWorldTransform(Matrix4 transform) {
        body.setWorldTransform(transform);
    }

    public void reset() {
        setWorldTransform(initialTransform);
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
        return motionState.transform.getTranslation(translation);
    }

    public Vector3 getScale() {
        return motionState.transform.getScale(scale);
    }

    public Quaternion getRotation() {
        return motionState.transform.getRotation(rotation);
    }

    private void buildBody(float mass, float friction, float restitution) {
        constructionInfo = getConstructionInfo(collisionShape, mass);

        if (constructionInfo != null) {
            motionState = new MotionState();

            body = new btRigidBody(constructionInfo);
            body.setSleepingThresholds(1f / 1000f, 1f / 1000f);
            body.setFriction(friction);
            body.setRestitution(restitution);

            if (kinematic) {
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
            }
        }
    }

    private void setTransform(Matrix4 transform) {
        motionState.transform.set(transform);
    }

    private void buildCollisionShape(Assets assets) {
        switch (rigidBodyShape.type) {
            case BOX:
                BoxRigidBodyShape boxRigidBodyShape = (BoxRigidBodyShape) rigidBodyShape;
                collisionShape = new btBoxShape(new Vector3(boxRigidBodyShape.width, boxRigidBodyShape.height, boxRigidBodyShape.depth));
                break;

            case CONE:
                ConeRigidBodyShape coneRigidBodyShape = (ConeRigidBodyShape) rigidBodyShape;
                collisionShape = new btConeShape(coneRigidBodyShape.radius, coneRigidBodyShape.height);
                break;

            case SPHERE:
                SphereRigidBodyShape sphereRigidBodyShape = (SphereRigidBodyShape) rigidBodyShape;
                collisionShape = new btSphereShape(sphereRigidBodyShape.radius);
                break;

            case CAPSULE:
                CapsuleRigidBodyShape capsuleRigidBodyShape = (CapsuleRigidBodyShape) rigidBodyShape;
                collisionShape = new btCapsuleShape(capsuleRigidBodyShape.radius, capsuleRigidBodyShape.height);
                break;

            case CYLINDER:
                CylinderRigidBodyShape cylinderRigidBodyShape = (CylinderRigidBodyShape) rigidBodyShape;
                collisionShape = new btCylinderShape(new Vector3(cylinderRigidBodyShape.width, cylinderRigidBodyShape.height, cylinderRigidBodyShape.depth));
                break;

            case CONVEX_HULL:
                ConvexHullRigidBodyShape convexHullRigidBodyShape = (ConvexHullRigidBodyShape) rigidBodyShape;

                Model convexHull = assets.get(convexHullRigidBodyShape.asset);
                Mesh convexHullMesh = convexHull.meshes.first();

                collisionShape = new btConvexHullShape(convexHullMesh.getVerticesBuffer(), convexHullMesh.getNumVertices(), convexHullMesh.getVertexSize());

                if (convexHullRigidBodyShape.optimize) {
                    ((btConvexHullShape) collisionShape).optimizeConvexHull();
                }
                break;

            case BVH_TRIANGLE_MESH:
                BvhTriangleMeshRigidBodyShape bvhTriangleMeshRigidBodyShape = (BvhTriangleMeshRigidBodyShape) rigidBodyShape;
                Model bvhTriangleModel = assets.get(bvhTriangleMeshRigidBodyShape.asset);
                collisionShape = new btBvhTriangleMeshShape(bvhTriangleModel.meshParts, bvhTriangleMeshRigidBodyShape.quantization, bvhTriangleMeshRigidBodyShape.buildBvh);
                break;

            case CONVEX_TRIANGLE_MESH:
                ConvexTriangleMeshRigidBodyShape convexTriangleMeshRigidBodyShape = (ConvexTriangleMeshRigidBodyShape) rigidBodyShape;
                Model convexTriangleModel = assets.get(convexTriangleMeshRigidBodyShape.asset);
                collisionShape = new btConvexTriangleMeshShape(btTriangleIndexVertexArray.obtain(convexTriangleModel.meshParts), convexTriangleMeshRigidBodyShape.calcAabb);
                break;
        }
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

    public enum State {
        NOT_INITIALIZED,
        READY
    }
}
