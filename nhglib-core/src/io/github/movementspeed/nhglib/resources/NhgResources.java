package io.github.movementspeed.nhglib.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Fausto Napoli on 18/08/2017.
 */
public class NhgResources {
    private static final String MODEL_QUAD = "io/github/movementspeed/nhglib/resources/models/quad.g3db";

    public static FileHandle getModelQuad() {
        return Gdx.files.classpath(NhgResources.MODEL_QUAD);
    }
}
