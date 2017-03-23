package io.github.voidzombie.nhglib.utils.graphics;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.graphics.lights.tiled.NhgLightsAttribute;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.PbrTextureAttribute;

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
public class ShaderUtils {
    public static String createPrefix(Renderable renderable, boolean skinned) {
        String prefix = "";
        final int n = renderable.meshPart.mesh.getVertexAttributes().size();

        for (int i = 0; i < n; i++) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);

            if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                prefix += "#define boneWeight" + attr.unit + "Flag\n";
            }
        }

        if (skinned) {
            prefix += "#define skinningFlag\n";
        }

        Environment environment = renderable.environment;

        // Ambient lighting
        ColorAttribute ambientLightAttribute = (ColorAttribute) environment.get(ColorAttribute.AmbientLight);

        if (ambientLightAttribute != null) {
            prefix += "#define ambientLighting\n";
        }

        // Directional lighting
        DirectionalLightsAttribute directionalLightsAttribute = (DirectionalLightsAttribute) environment.get(DirectionalLightsAttribute.Type);

        if (directionalLightsAttribute != null) {
            Array<DirectionalLight> directionalLights = directionalLightsAttribute.lights;

            if (directionalLights.size > 0) {
                prefix += "#define numDirectionalLights " + directionalLights.size + "\n";
            }
        }

        // Point lighting
        PointLightsAttribute pointLightsAttribute = (PointLightsAttribute) environment.get(PointLightsAttribute.Type);

        if (pointLightsAttribute != null) {
            Array<PointLight> pointLights = pointLightsAttribute.lights;

            if (pointLights.size > 0) {
                prefix += "#define numPointLights " + pointLights.size + "\n";
            }
        }

        // Spot lighting
        SpotLightsAttribute spotLightsAttribute = (SpotLightsAttribute) environment.get(SpotLightsAttribute.Type);

        if (spotLightsAttribute != null) {
            Array<SpotLight> spotLights = spotLightsAttribute.lights;

            if (spotLights.size > 0) {
                prefix += "#define numSpotLights " + spotLights.size + "\n";
            }
        }

        return prefix;
    }

    public static boolean isRenderableSkinned(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }

    public static boolean hasDiffuse(Renderable renderable) {
        boolean res = false;
        TextureAttribute attribute = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasNormal(Renderable renderable) {
        boolean res = false;
        TextureAttribute attribute = (TextureAttribute) renderable.material.get(TextureAttribute.Normal);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasSpecular(Renderable renderable) {
        boolean res = false;
        TextureAttribute attribute = (TextureAttribute) renderable.material.get(TextureAttribute.Specular);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasBump(Renderable renderable) {
        boolean res = false;
        TextureAttribute attribute = (TextureAttribute) renderable.material.get(TextureAttribute.Bump);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasAlbedo(Renderable renderable) {
        boolean res = false;
        PbrTextureAttribute attribute = (PbrTextureAttribute) renderable.material.get(PbrTextureAttribute.Albedo);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasMetalness(Renderable renderable) {
        boolean res = false;
        PbrTextureAttribute attribute = (PbrTextureAttribute) renderable.material.get(PbrTextureAttribute.Metalness);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasRoughness(Renderable renderable) {
        boolean res = false;
        PbrTextureAttribute attribute = (PbrTextureAttribute) renderable.material.get(PbrTextureAttribute.Roughness);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean hasAmbientOcclusion(Renderable renderable) {
        boolean res = false;
        PbrTextureAttribute attribute = (PbrTextureAttribute) renderable.material.get(PbrTextureAttribute.AmbientOcclusion);

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true;
        }

        return res;
    }

    public static boolean useBones(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }

    public static boolean hasLights(Environment environment) {
        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        return lightsAttribute != null && lightsAttribute.lights.size > 0;
    }
}