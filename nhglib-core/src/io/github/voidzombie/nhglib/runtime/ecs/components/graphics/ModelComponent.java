package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.serialization.PbrMaterialJson;

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
public class ModelComponent extends Component {
    public Type type;
    public State state;
    public Asset asset;

    public ModelInstance model;
    public AnimationController animationController;
    public Array<PbrMaterialJson> pbrMaterials;

    public ModelComponent() {
        pbrMaterials = new Array<>();
        state = State.NOT_INITIALIZED;
        type = Type.DYNAMIC;
    }

    public enum State {
        NOT_INITIALIZED,
        LOADING,
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
