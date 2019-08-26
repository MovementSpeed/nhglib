package io.github.movementspeed.nhglib.utils.graphics

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
object GLUtils {
    val isFloatTextureSupported: Boolean
        get() = Gdx.graphics.supportsExtension("OES_texture_float") || Gdx.app.type == Application.ApplicationType.Desktop

    fun clearScreen(color: Color = Color.BLACK, mask: Int = GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
        Gdx.gl.glClear(mask)
    }

    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        Gdx.gl.glViewport(x, y, width, height)
    }

    fun setViewport(width: Int, height: Int) {
        setViewport(0, 0, width, height)
    }
}
