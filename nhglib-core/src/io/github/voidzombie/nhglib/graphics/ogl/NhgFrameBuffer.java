package io.github.voidzombie.nhglib.graphics.ogl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class NhgFrameBuffer {


    public boolean depth;
    public boolean stencil;
    public boolean hasDepthStencilPackedBuffer;
    public boolean defaultFramebufferHandleInitialized;

    public int frameBufferHandle;
    public int depthBufferHandle;
    public int stencilBufferHandle;
    public int depthStencilPackedBufferHandle;
    public int defaultFramebufferHandle;
    public int width;
    public int height;
    public int glInternalFormat;
    public int glFormat;
    public int glType;

    public Type type;
    public Texture texture;

    public void init() {
        if (!defaultFramebufferHandleInitialized) {
            defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                IntBuffer intbuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();
                Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intbuf);
                defaultFramebufferHandle = intbuf.get(0);
            } else {
                defaultFramebufferHandle = 0;
            }
        }

        if (type != Type.CUSTOM) {
            if (type == Type.DEPTH) {
                glInternalFormat = GL20.GL_DEPTH_COMPONENT16;
                glFormat = GL20.GL_DEPTH_COMPONENT;
                glType = GL20.GL_UNSIGNED_INT;
            } else {
                glInternalFormat = GL20.GL_RGBA;
                glFormat = glInternalFormat;
                glType = GL20.GL_UNSIGNED_BYTE;
            }
        }

        GLOnlyTextureData data = new GLOnlyTextureData(
                width, height, 0,
                glInternalFormat, glFormat, glType);

        texture = new Texture(data);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        frameBufferHandle = Gdx.gl.glGenFramebuffer();

        if (depth) {
            depthBufferHandle = Gdx.gl.glGenRenderbuffer();
        }

        if (stencil) {
            stencilBufferHandle = Gdx.gl.glGenRenderbuffer();
        }

        Gdx.gl.glBindTexture(texture.glTarget, texture.getTextureObjectHandle());

        if (depth) {
            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthBufferHandle);
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16,
                    texture.getWidth(), texture.getHeight());
        }

        if (stencil) {
            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, stencilBufferHandle);
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_STENCIL_INDEX8,
                    texture.getWidth(), texture.getHeight());
        }

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, frameBufferHandle);

        if (type == Type.DEPTH) {
            Gdx.gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER,
                    GL20.GL_DEPTH_ATTACHMENT, GL20.GL_TEXTURE_2D,
                    texture.getTextureObjectHandle(), 0);
        } else {
            Gdx.gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER,
                    GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
                    texture.getTextureObjectHandle(), 0);
        }

        if (depth) {
            Gdx.gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER,
                    GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER,
                    depthBufferHandle);
        }

        if (stencil) {
            Gdx.gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER,
                    GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER,
                    stencilBufferHandle);
        }

        Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
        Gdx.gl.glBindTexture(texture.glTarget, 0);

        int result = Gdx.gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);

        if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED && depth && stencil
                && (Gdx.graphics.supportsExtension("GL_OES_packed_depth_stencil") ||
                Gdx.graphics.supportsExtension("GL_EXT_packed_depth_stencil"))) {
            if (depth) {
                Gdx.gl.glDeleteRenderbuffer(depthBufferHandle);
                depthBufferHandle = 0;
            }

            if (stencil) {
                Gdx.gl.glDeleteRenderbuffer(stencilBufferHandle);
                stencilBufferHandle = 0;
            }

            depthStencilPackedBufferHandle = Gdx.gl.glGenRenderbuffer();
            hasDepthStencilPackedBuffer = true;
            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthStencilPackedBufferHandle);
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GLOES.GL_DEPTH24_STENCIL8_OES, texture.getWidth(), texture.getHeight());
            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);

            Gdx.gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER, depthStencilPackedBufferHandle);
            Gdx.gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER, depthStencilPackedBufferHandle);
            result = Gdx.gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
        }

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);

        if (result != GL20.GL_FRAMEBUFFER_COMPLETE) {
            texture.dispose();

            if (hasDepthStencilPackedBuffer) {
                Gdx.gl.glDeleteBuffer(depthStencilPackedBufferHandle);
            } else {
                if (depth) Gdx.gl.glDeleteRenderbuffer(depthBufferHandle);
                if (stencil) Gdx.gl.glDeleteRenderbuffer(stencilBufferHandle);
            }

            Gdx.gl.glDeleteFramebuffer(frameBufferHandle);

            if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
                throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment");
            if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS)
                throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions");
            if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT)
                throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment");
            if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED)
                throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats");
            throw new IllegalStateException("frame buffer couldn't be constructed: unknown error " + result);
        }
    }

    public void begin() {
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, frameBufferHandle);
        Gdx.gl20.glViewport(0, 0, texture.getWidth(), texture.getHeight());
    }

    public void end() {
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    public void dispose() {
        GL20 gl = Gdx.gl20;

        texture.dispose();

        if (hasDepthStencilPackedBuffer) {
            gl.glDeleteRenderbuffer(depthStencilPackedBufferHandle);
        } else {
            if (depth) gl.glDeleteRenderbuffer(depthBufferHandle);
            if (stencil) gl.glDeleteRenderbuffer(stencilBufferHandle);
        }

        gl.glDeleteFramebuffer(frameBufferHandle);
    }

    public enum Type {
        COLOR,
        DEPTH,
        CUSTOM
    }
}
