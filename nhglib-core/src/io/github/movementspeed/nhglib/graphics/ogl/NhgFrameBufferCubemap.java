package io.github.movementspeed.nhglib.graphics.ogl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NhgFrameBufferCubemap extends GLFrameBuffer<Cubemap> {
    public boolean genMipMap;

    /**
     * 0: Default format
     * 1: Float format
     */
    public int type = 0;

    /**
     * the zero-based index of the active side
     **/
    private int currentSide;

    /**
     * Creates a new FrameBuffer having the given dimensions and potentially a depth buffer attached.
     *
     * @param format
     * @param width
     * @param height
     * @param hasDepth
     */
    public NhgFrameBufferCubemap(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false, false, 0);
    }

    /**
     * Creates a new FrameBuffer having the given dimensions and potentially a depth and a stencil buffer attached.
     *
     * @param format     the format of the color buffer; according to the OpenGL ES 2.0 spec, only RGB565, RGBA4444 and RGB5_A1 are
     *                   color-renderable
     * @param width      the width of the cubemap in pixels
     * @param height     the height of the cubemap in pixels
     * @param hasDepth   whether to attach a depth buffer
     * @param hasStencil whether to attach a stencil buffer
     * @throws com.badlogic.gdx.utils.GdxRuntimeException in case the FrameBuffer could not be created
     */
    public NhgFrameBufferCubemap(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil, boolean genMipMap, int textureType) {
        super(format, width, height, hasDepth, hasStencil);
        this.genMipMap = genMipMap;
        this.type = textureType;
    }

    /**
     * Makes the frame buffer current so everything gets drawn to it, must be followed by call to either {@link #nextSide(int mipLevel)} or
     * {@link #bindSide(com.badlogic.gdx.graphics.Cubemap.CubemapSide, int mipLevel)} to activate the side to render onto.
     */
    @Override
    public void bind() {
        currentSide = -1;
        super.bind();
    }

    @Override
    protected void disposeColorTexture(Cubemap colorTexture) {
        colorTexture.dispose();
    }

    @Override
    protected void attachFrameBufferColorTexture() {
        GL20 gl = Gdx.gl20;
        int glHandle = colorTexture.getTextureObjectHandle();
        Cubemap.CubemapSide[] sides = Cubemap.CubemapSide.values();
        for (Cubemap.CubemapSide side : sides) {
            gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum,
                    glHandle, 0);
        }
    }

    @Override
    protected void build() {
        // Do nothing, use buildFBO after initialization
    }

    @Override
    protected Cubemap createColorTexture() {
        TextureData data = null;

        if (!Gdx.graphics.supportsExtension("OES_texture_float") && Gdx.app.getType() != Application.ApplicationType.Desktop) {
            type = 0;
        }

        switch (type) {
            case 0:
                int glFormat = Pixmap.Format.toGlFormat(format);
                int glType = Pixmap.Format.toGlType(format);
                data = new NhgGLOnlyTextureData(width, height, 0, glFormat, glFormat, glType, true);
                break;

            case 1:
                data = new NhgFloatTextureData(width, height, 3);
                break;
        }

        Cubemap result = new Cubemap(data, data, data, data, data, data);

        Texture.TextureFilter minFilter = genMipMap ? Texture.TextureFilter.MipMapLinearLinear : Texture.TextureFilter.Linear;

        result.setFilter(minFilter, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        if (genMipMap) {
            Gdx.gl.glBindTexture(result.glTarget, result.getTextureObjectHandle());
            Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
        }

        return result;
    }

    /**
     * Bind the side, making it active to render on. Should be called in between a call to {@link #begin()} and {@link #end()}.
     */
    public void bindSide(int sideN, int mipLevel) {
        currentSide = sideN;
        Cubemap.CubemapSide side = getSide();

        Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum,
                colorTexture.getTextureObjectHandle(), mipLevel);
    }

    public void buildFBO() {
        super.build();
    }

    public void setGenMipMap(boolean genMipMap) {
        this.genMipMap = genMipMap;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * Bind the next side of cubemap and return false if no more side. Should be called in between a call to {@link #begin()} and
     * #end to cycle to each side of the cubemap to render on.
     */
    public boolean nextSide(int mipLevel) {
        if (currentSide > 5) {
            throw new GdxRuntimeException("No remaining sides.");
        } else if (currentSide == 5) {
            return false;
        }

        currentSide++;
        bindSide(getSide(), mipLevel);
        return true;
    }

    /**
     * Bind the side, making it active to render on. Should be called in between a call to {@link #begin()} and {@link #end()}.
     *
     * @param side The side to bind
     */
    private void bindSide(final Cubemap.CubemapSide side, int mipLevel) {
        Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum,
                colorTexture.getTextureObjectHandle(), mipLevel);
    }

    /**
     * Get the currently bound side.
     */
    private Cubemap.CubemapSide getSide() {
        return currentSide < 0 ? null : Cubemap.CubemapSide.values()[currentSide];
    }
}
