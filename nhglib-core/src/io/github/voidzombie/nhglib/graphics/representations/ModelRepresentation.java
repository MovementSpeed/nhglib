package io.github.voidzombie.nhglib.graphics.representations;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;

/**
 * Created by Fausto Napoli on 09/12/2016.
 */
public class ModelRepresentation implements Representation<ModelInstance> {
    private ModelInstance modelInstance;

    @Override
    public ModelInstance get() {
        return modelInstance;
    }
}
