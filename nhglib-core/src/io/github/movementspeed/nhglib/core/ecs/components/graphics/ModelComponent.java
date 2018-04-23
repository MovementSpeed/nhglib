package io.github.movementspeed.nhglib.core.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.utils.PbrMaterial;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class ModelComponent extends Component {
    public boolean enabled;
    public boolean nodeAdded;

    public float radius;

    public Type type;
    public State state;
    public String asset;
    public ModelInstance model;
    public BoundingBox boundingBox;
    public AnimationController animationController;

    public Array<PbrMaterial> pbrMaterials;

    public ModelComponent() {
        pbrMaterials = new Array<>();
        enabled = true;
        nodeAdded = false;
        state = State.NOT_INITIALIZED;
        type = Type.DYNAMIC;
    }

    public void buildWithModel(Model m) {
        buildWithModel(new ModelInstance(m));
    }

    public void buildWithModel(ModelInstance m) {
        model = m;
        boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);

        Vector3 dimensions = boundingBox.getDimensions(new Vector3());
        radius = dimensions.len() / 2f;

        state = ModelComponent.State.READY;

        if (m.animations.size > 0) {
            animationController = new AnimationController(model);
        }
    }

    public void buildWithAsset(String asset) {
        this.asset = asset;
        this.state = State.NOT_INITIALIZED;
    }

    public void setPbrMaterial(PbrMaterial material) {
        for (Material m : model.materials) {
            m.set(material);
        }
    }

    public void setPbrMaterial(int index, PbrMaterial material) {
        model.materials.get(index).set(material);
    }

    public void setPbrMaterial(String nodeId, PbrMaterial material) {
        Node targetNode = model.getNode(nodeId);

        for (NodePart nodePart : targetNode.parts) {
            nodePart.material = material;
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
