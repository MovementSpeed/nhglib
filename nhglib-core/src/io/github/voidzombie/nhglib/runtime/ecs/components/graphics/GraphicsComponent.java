package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsComponent extends PooledComponent {
    public State state;
    public Asset asset;

    private Representation representation;

    public GraphicsComponent() {
        state = State.NOT_INITIALIZED;
    }

    @Override
    protected void reset() {
        state = State.NOT_INITIALIZED;
        asset = null;
        representation = null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Representation> T getRepresentation() {
        return (T) representation;
    }

    public void setRepresentation(Representation representation) {
        if (representation != null) {
            this.representation = representation;
            state = State.READY;
        }
    }

    public enum State {
        NOT_INITIALIZED,
        LOADING,
        READY
    }
}
