package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class DepthPreRenderPass extends RenderPass {
    public FrameBuffer forwardFBO;

    public DepthPreRenderPass() {
        super(true);
    }

    @Override
    public void created() {
        setShaderProvider(new SimpleShaderProvider());

        GLFrameBuffer.FrameBufferBuilder bufferBuilder = new GLFrameBuffer.FrameBufferBuilder(
                mainFBO.getWidth(), mainFBO.getHeight());
        bufferBuilder.addColorTextureAttachment(GL30.GL_RGBA, GL30.GL_RGBA, GL30.GL_FLOAT);
        bufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT32F, GL30.GL_FLOAT);
        forwardFBO = bufferBuilder.build();

        Texture depthTexture = forwardFBO.getTextureAttachments().get(1);
        bundle.put("depthTexture", depthTexture);
    }

    @Override
    public void begin(PerspectiveCamera camera) {
        forwardFBO.begin();
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        renderer.render(renderableProviders);
    }

    @Override
    public void end() {
        forwardFBO.end();
    }
}
