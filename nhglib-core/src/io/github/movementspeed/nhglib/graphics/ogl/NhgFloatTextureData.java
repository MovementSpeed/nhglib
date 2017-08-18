package io.github.movementspeed.nhglib.graphics.ogl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.FloatBuffer;

/**
 * A {@link TextureData} implementation which should be used to create float textures.
 */
public class NhgFloatTextureData implements TextureData {

    int width = 0;
    int height = 0;
    boolean isPrepared = false;
    FloatBuffer buffer;

    public NhgFloatTextureData(int w, int h) {
        this.width = w;
        this.height = h;
    }

    @Override
    public TextureDataType getType() {
        return TextureDataType.Custom;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void prepare() {
        if (isPrepared) throw new GdxRuntimeException("Already prepared");
        this.buffer = BufferUtils.newFloatBuffer(width * height * 4);
        isPrepared = true;
    }

    @Override
    public void consumeCustomData(int target) {
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS
                || Gdx.app.getType() == Application.ApplicationType.WebGL) {

            if (!Gdx.graphics.supportsExtension("OES_texture_float"))
                throw new GdxRuntimeException("Extension OES_texture_float not supported!");

            // GLES and WebGL defines texture format by 3rd and 8th argument,
            // so to get a float texture one needs to supply GL_RGBA and GL_FLOAT there.
            Gdx.gl.glTexImage2D(target, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGB, GL20.GL_FLOAT, buffer);

        } else {
            if (!Gdx.graphics.supportsExtension("GL_ARB_texture_float"))
                throw new GdxRuntimeException("Extension GL_ARB_texture_float not supported!");

            final int GL_RGBA32F = 34836; // this is a const from GL 3.0, used only on desktops

            // in desktop OpenGL the texture format is defined only by the third argument,
            // hence we need to use GL_RGBA32F there (this constant is unavailable in GLES/WebGL)
            Gdx.gl.glTexImage2D(target, 0, GL_RGBA32F, width, height, 0, GL20.GL_RGBA, GL20.GL_FLOAT, buffer);
        }
    }

    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    @Override
    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Pixmap.Format getFormat() {
        return Pixmap.Format.RGB888; // it's not true, but FloatTextureData.getFormat() isn't used anywhere
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return true;
    }

    public FloatBuffer getBuffer() {
        return buffer;
    }
}
