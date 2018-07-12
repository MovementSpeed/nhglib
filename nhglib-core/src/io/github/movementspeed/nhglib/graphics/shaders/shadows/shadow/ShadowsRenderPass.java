package io.github.movementspeed.nhglib.graphics.shaders.shadows.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.ShadowsAttribute;

public class ShadowsRenderPass extends RenderPass {
    private FrameBuffer frameBuffer;
    private ShadowsAttribute shadowsAttribute;
    private Array<NhgLight> shadowLights;

    public ShadowsRenderPass() {
        super(false);
    }

    @Override
    public void created() {
        shadowsAttribute = (ShadowsAttribute) environment.get(ShadowsAttribute.Type);
        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        Array<NhgLight> lights = lightsAttribute.lights;
        shadowLights = new Array<>();

        for (NhgLight light : lights) {
            if (light.castsShadows()) {
                shadowLights.add(light);
            }
        }

        setShaderProvider(new ShadowsShaderProvider(shadowLights));
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void begin(PerspectiveCamera camera) {
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        frameBuffer.begin();

        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.begin(camera);
        renderer.render(renderableProviders, environment);
        renderer.end();

        frameBuffer.end();

        if (shadowsAttribute != null) {
            shadowsAttribute.shadows = frameBuffer.getColorBufferTexture();
        } else {
            shadowsAttribute = new ShadowsAttribute(frameBuffer.getColorBufferTexture());
            environment.set(shadowsAttribute);
        }
    }

    @Override
    public void end() {
        mainFBO.begin();
    }
}
