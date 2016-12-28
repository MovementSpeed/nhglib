package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.PooledComponent;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsComponent extends PooledComponent {
    public Type type;
    public State state;
    public Asset asset;

    private Representation representation;

    public GraphicsComponent() {
        state = State.NOT_INITIALIZED;
    }

    @Override
    protected void reset() {
        invalidate();
        asset = null;
    }

    public void invalidate() {
        state = State.NOT_INITIALIZED;
        representation.invalidate();
        representation = null;
    }

    public void setRepresentation(Representation representation) {
        if (representation != null) {
            state = State.READY;
            this.representation = representation;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Representation> T getRepresentation() {
        return (T) representation;
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
