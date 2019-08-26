package io.github.movementspeed.nhglib.graphics.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
class DefaultPerspectiveCamera : PerspectiveCamera() {
    init {
        fieldOfView = 67f

        viewportWidth = Gdx.graphics.width.toFloat()
        viewportHeight = Gdx.graphics.height.toFloat()

        near = 0.01f
        far = 100.0f

        update()
    }
}
