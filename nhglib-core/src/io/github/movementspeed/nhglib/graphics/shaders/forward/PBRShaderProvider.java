package io.github.movementspeed.nhglib.graphics.shaders.forward;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import io.github.movementspeed.nhglib.utils.data.Bundle;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class PBRShaderProvider extends BaseShaderProvider {

    public PBRShaderProvider() {
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        PBRShader.Params params = new PBRShader.Params();
        params.albedo = ShaderUtils.hasAlbedo(renderable);
        params.metalness = ShaderUtils.hasMetalness(renderable);
        params.roughness = ShaderUtils.hasRoughness(renderable);
        params.normal = ShaderUtils.hasPbrNormal(renderable);
        params.ambientOcclusion = ShaderUtils.hasAmbientOcclusion(renderable);
        params.useBones = ShaderUtils.useBones(renderable);
        params.lit = ShaderUtils.hasLights(renderable.environment);
        params.gammaCorrection = ShaderUtils.useGammaCorrection(renderable.environment);
        params.imageBasedLighting = ShaderUtils.useImageBasedLighting(renderable.environment);

        if (renderable.userData != null) {
            Bundle bundle = (Bundle) renderable.userData;
            boolean forceUnlit = bundle.getBoolean(Strings.RenderingSettings.forceUnlitKey, false);

            if (forceUnlit) {
                params.lit = false;
                params.imageBasedLighting = false;
            }
        }

        return new PBRShader(renderable, renderable.environment, params);
    }
}