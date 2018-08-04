package io.github.movementspeed.nhglib.graphics.shaders.shadows.depth;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class LightDepthMapRenderPass extends RenderPass {
    private Array<NhgLight> shadowLights;

    public LightDepthMapRenderPass() {
        super(false);
    }

    @Override
    public void created() {
        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        Array<NhgLight> lights = lightsAttribute.lights;
        shadowLights = new Array<>();

        for (NhgLight light : lights) {
            if (light.castsShadows()) {
                shadowLights.add(light);
            }
        }

        setShaderProvider(new LightDepthMapShaderProvider(shadowLights));
    }

    @Override
    public void begin(PerspectiveCamera camera) {
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        Array<NhgLight> lights = lightsAttribute.lights;
        if (lights.size != shadowLights.size) {
            shadowLights.clear();

            for (NhgLight light : lights) {
                if (light.castsShadows()) {
                    shadowLights.add(light);
                }
            }
        }

        for (NhgLight light : shadowLights) {
            switch (light.type) {
                case POINT_LIGHT:
                    for (int i = 0; i < 6; i++) {
                        light.shadowLightProperties.begin();
                        renderer.begin(light.shadowLightProperties.lightCamera);
                        renderer.render(renderableProviders, environment);
                        renderer.end();
                    }
                    light.shadowLightProperties.end();
                    break;

                default:
                    light.shadowLightProperties.begin();
                    renderer.begin(light.shadowLightProperties.lightCamera);
                    renderer.render(renderableProviders, environment);
                    renderer.end();
                    light.shadowLightProperties.end();
            }
        }
    }

    @Override
    public void end() {
    }
}
