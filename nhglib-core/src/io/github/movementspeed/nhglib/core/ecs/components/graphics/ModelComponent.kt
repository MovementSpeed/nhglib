package io.github.movementspeed.nhglib.core.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.utils.PBRMaterial;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class ModelComponent extends Component {
    public boolean enabled;
    public boolean nodeAdded;
    public boolean cached;

    public float radius;

    public Type type;
    public State state;
    public String asset;
    public ModelInstance model;
    public BoundingBox boundingBox;
    public AnimationController animationController;

    public Array<PBRMaterial> pbrMaterials;

    private Vector3 translationBefore, translationAfter, scaleBefore, scaleAfter, rotationBefore, rotationAfter;
    private Quaternion rotationQuaternionBefore, rotationQuaternionAfter;

    public ModelComponent() {
        pbrMaterials = new Array<>();
        enabled = true;
        nodeAdded = false;
        state = State.NOT_INITIALIZED;
        type = Type.DYNAMIC;

        translationBefore = new Vector3();
        translationAfter = new Vector3();
        scaleBefore = new Vector3();
        scaleAfter = new Vector3();
        rotationBefore = new Vector3();
        rotationAfter = new Vector3();
        rotationQuaternionBefore = new Quaternion();
        rotationQuaternionAfter = new Quaternion();
    }

    public void buildWithModel(Model m) {
        buildWithModel(new ModelInstance(m));
    }

    public void buildWithModel(Vector3 nodeScale, Model m) {
        buildWithModel(nodeScale, new ModelInstance(m));
    }

    public void buildWithModel(ModelInstance m) {
        buildWithModel(new Vector3(1, 1, 1), m);
    }

    public void buildWithModel(Vector3 nodeScale, ModelInstance m) {
        model = m;
        boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);

        Vector3 dimensions = boundingBox.getDimensions(new Vector3());
        dimensions.scl(nodeScale);
        radius = dimensions.len() / 2f;

        state = ModelComponent.State.READY;

        if (m.animations.size > 0) {
            animationController = new AnimationController(model);
        }
    }

    public void calculateTransforms() {
        if (type == Type.STATIC) {
            model.transform.getTranslation(translationBefore);
            model.transform.getScale(scaleBefore);
            model.transform.getRotation(rotationQuaternionBefore);
            rotationBefore.set(rotationQuaternionBefore.getPitch(), rotationQuaternionBefore.getYaw(), rotationQuaternionBefore.getRoll());
        }

        model.calculateTransforms();

        if (type == Type.STATIC) {
            model.transform.getTranslation(translationAfter);
            model.transform.getScale(scaleAfter);
            model.transform.getRotation(rotationQuaternionAfter);
            rotationAfter.set(rotationQuaternionAfter.getPitch(), rotationQuaternionAfter.getYaw(), rotationQuaternionAfter.getRoll());

            translationBefore.sub(translationAfter);
            scaleBefore.sub(scaleAfter);
            rotationBefore.sub(rotationAfter);

            if (translationBefore.x != 0 || translationBefore.y != 0 || translationBefore.z != 0 ||
                    scaleBefore.x != 0 || scaleBefore.y != 0 || scaleBefore.z != 0 ||
                    rotationBefore.x != 0 || rotationBefore.y != 0 || rotationBefore.z != 0) {
                cached = false;
            }
        }
    }

    public void buildWithAsset(String asset) {
        this.asset = asset;
        this.state = State.NOT_INITIALIZED;
    }

    public void setPBRMaterial(PBRMaterial material) {
        for (Material m : model.materials) {
            m.set(material);
        }
    }

    public void setPBRMaterial(int index, PBRMaterial material) {
        model.materials.get(index).set(material);
    }

    public void setPBRMaterial(String nodeId, PBRMaterial material) {
        Node targetNode = model.getNode(nodeId);

        for (NodePart nodePart : targetNode.parts) {
            nodePart.material.set(material);
        }
    }

    public enum State {
        NOT_INITIALIZED,
        READY
    }

    public enum Type {
        STATIC,
        DYNAMIC;

        public static Type fromString(String value) {
            Type type = null;

            if (value.contentEquals("dynamic")) {
                type = DYNAMIC;
            } else if (value.contentEquals("static")) {
                type = STATIC;
            }

            return type;
        }
    }
}
