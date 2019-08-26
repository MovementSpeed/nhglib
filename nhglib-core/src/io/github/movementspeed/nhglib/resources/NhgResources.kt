package io.github.movementspeed.nhglib.resources

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

/**
 * Created by Fausto Napoli on 18/08/2017.
 */
object NhgResources {
    private val MODEL_QUAD = "io/github/movementspeed/nhglib/resources/models/quad.g3db"

    val modelQuad: FileHandle
        get() = Gdx.files.classpath(MODEL_QUAD)
}
