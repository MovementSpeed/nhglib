package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.PooledComponent;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsComponent extends PooledComponent {
    private Representation representation;

    @Override
    protected void reset() {
    }

    @SuppressWarnings("unchecked")
    public <T extends Representation> T getRepresentation() {
        return (T) representation;
    }
}
