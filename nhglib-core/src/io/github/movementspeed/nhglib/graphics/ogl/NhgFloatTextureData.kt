package io.github.movementspeed.nhglib.graphics.ogl

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.TextureData
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.GdxRuntimeException

import java.nio.FloatBuffer

/**
 * A [TextureData] implementation which should be used to create float textures.
 */
class NhgFloatTextureData @JvmOverloads constructor(w: Int, h: Int, private val numComponents: Int, private val internalFormat: Int = GL20.GL_RGB, private val format: Int = GL20.GL_RGB) : TextureData {
    private var isPrepared = false

    private val width = 0
    private val height = 0
    var buffer: FloatBuffer? = null
        private set

    init {
        this.width = w
        this.height = h
    }

    override fun getType(): TextureData.TextureDataType {
        return TextureData.TextureDataType.Custom
    }

    override fun isPrepared(): Boolean {
        return isPrepared
    }

    override fun prepare() {
        if (isPrepared) throw GdxRuntimeException("Already prepared")
        this.buffer = BufferUtils.newFloatBuffer(width * height * numComponents)
        isPrepared = true
    }

    override fun consumeCustomData(target: Int) {
        if (Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.iOS
                || Gdx.app.type == Application.ApplicationType.WebGL) {

            if (!Gdx.graphics.supportsExtension("OES_texture_float"))
                throw GdxRuntimeException("Extension OES_texture_float not supported!")

            // GLES and WebGL defines texture format by 3rd and 8th argument,
            // so to get a float texture one needs to supply GL_RGBA and GL_FLOAT there.
            Gdx.gl.glTexImage2D(target, 0, internalFormat, width, height, 0, format, GL20.GL_FLOAT, buffer)
        } else {
            if (!Gdx.graphics.supportsExtension("GL_ARB_texture_float"))
                throw GdxRuntimeException("Extension GL_ARB_texture_float not supported!")

            // in desktop OpenGL the texture format is defined only by the third argument,
            // hence we need to use GL_RGBA32F there (this constant is unavailable in GLES/WebGL)
            Gdx.gl.glTexImage2D(target, 0, internalFormat, width, height, 0, format, GL20.GL_FLOAT, buffer)
        }
    }

    override fun consumePixmap(): Pixmap {
        throw GdxRuntimeException("This TextureData implementation does not return a Pixmap")
    }

    override fun disposePixmap(): Boolean {
        throw GdxRuntimeException("This TextureData implementation does not return a Pixmap")
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }

    override fun getFormat(): Pixmap.Format {
        return Pixmap.Format.RGB888 // it's not true, but FloatTextureData.getFormat() isn't used anywhere
    }

    override fun useMipMaps(): Boolean {
        return false
    }

    override fun isManaged(): Boolean {
        return true
    }
}
