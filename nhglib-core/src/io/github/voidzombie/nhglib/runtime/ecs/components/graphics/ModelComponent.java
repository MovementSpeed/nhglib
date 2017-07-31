package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.graphics.utils.PbrMaterial;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class ModelComponent extends Component {
    public boolean enabled;
    public boolean nodeAdded;

    public Type type;
    public State state;
    public String asset;
    public ModelInstance model;
    public AnimationController animationController;

    public Array<PbrMaterial> pbrMaterials;

    public ModelComponent() {
        pbrMaterials = new Array<>();
        enabled = true;
        nodeAdded = false;
        state = State.NOT_INITIALIZED;
        type = Type.DYNAMIC;
    }

    public void initWithModel(Model m) {
        model = new ModelInstance(m);
        state = ModelComponent.State.READY;

        if (m.animations.size > 0) {
            animationController = new AnimationController(model);
        }
    }

    public void initWithAsset(String asset) {
        this.asset = asset;
        this.state = State.NOT_INITIALIZED;
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
