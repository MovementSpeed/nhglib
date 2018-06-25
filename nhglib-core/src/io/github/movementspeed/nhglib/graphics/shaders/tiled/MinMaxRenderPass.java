package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.LightGrid;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.MinMax;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

import java.nio.FloatBuffer;

public class MinMaxRenderPass extends RenderPass {
    private FrameBuffer minMaxDepthFBO;
    private Texture depthTexture;
    private MinMaxShaderProvider minMaxShaderProvider;

    private Array<MinMax> tileDepthRanges;

    public MinMaxRenderPass() {
        super(true);
        tileDepthRanges = new Array<>();
        bundle.put("tileDepthRanges", tileDepthRanges);
    }

    @Override
    public void created() {
        setShaderProvider(new MinMaxShaderProvider());
        minMaxShaderProvider = (MinMaxShaderProvider) shaderProvider;

        GLFrameBuffer.FrameBufferBuilder bufferBuilder = new GLFrameBuffer.FrameBufferBuilder(
                mainFBO.getWidth(), mainFBO.getHeight());
        bufferBuilder.addColorTextureAttachment(GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_FLOAT);
        minMaxDepthFBO = bufferBuilder.build();

        depthTexture = (Texture) previousRenderPass.bundle.get("depthTexture");
        previousRenderPass.bundle.clear();

        minMaxShaderProvider.setDepthTexture(depthTexture);
        depthTexture = null;
    }

    @Override
    public void begin(PerspectiveCamera camera) {
        minMaxDepthFBO.begin();
        GLUtils.clearScreen(Color.BLACK);
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        renderer.render(renderableProviders);
    }

    @Override
    public void end() {
        //calcMinMaxDepth();
    }

    private Pixmap pixmap = new Pixmap((int) LightGrid.gridSize.x, (int) LightGrid.gridSize.y, Pixmap.Format.RGB888);

    private void calcMinMaxDepth() {
        tileDepthRanges.clear();
        int bufferSize = (int) LightGrid.gridSize.x * (int) LightGrid.gridSize.y;
        FloatBuffer buffer = pixmap.getPixels().asFloatBuffer();

        Gdx.gl.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, 0);
        Gdx.gl.glReadPixels(0, 0, (int) LightGrid.gridSize.x, (int) LightGrid.gridSize.y, GL30.GL_RG, GL30.GL_FLOAT, buffer);

        for (int i = 0; i < bufferSize; i += 4) {
            MinMax minMax = new MinMax();
            minMax.min = buffer.get(i);
            minMax.max = buffer.get(i + 1);

            MinMax result = new MinMax();
            result.min = buffer.get(i + 2);
            result.max = buffer.get(i + 3);

            tileDepthRanges.addAll(minMax, result);
        }

        mainFBO.begin();
    }
}
