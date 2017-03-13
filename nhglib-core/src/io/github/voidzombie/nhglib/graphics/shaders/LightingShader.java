package io.github.voidzombie.nhglib.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.voidzombie.nhglib.graphics.lights.IntensityDirectionalLight;
import io.github.voidzombie.nhglib.graphics.lights.RadiusPointLight;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
public class LightingShader extends BaseShader {
    private boolean skinned;

    private int firstDirLightsColor = register(new Uniform("u_dirLights[0].color"));
    private int firstDirLightsDirection = register(new Uniform("u_dirLights[0].direction"));
    private int firstDirLightsIntensity = register(new Uniform("u_dirLights[0].intensity"));
    private int secondDirLightsColor = register(new Uniform("u_dirLights[1].color"));

    private int ambientLightColor = register(new Uniform("u_ambientLightColor"));

    private int firstPntLightsColor = register(new Uniform("u_pointLights[0].color"));
    private int firstPntLightsPosition = register(new Uniform("u_pointLights[0].position"));
    private int firstPntLightsIntensity = register(new Uniform("u_pointLights[0].intensity"));
    private int firstPntLightsRadius = register(new Uniform("u_pointLights[0].radius"));
    private int secondPntLightsColor = register(new Uniform("u_pointLights[1].color"));

    private int firstSptLightsColor = register(new Uniform("u_spotLights[0].color"));
    private int firstSptLightsPosition = register(new Uniform("u_spotLights[0].position"));
    private int firstSptLightsDirection = register(new Uniform("u_spotLights[0].direction"));
    private int firstSptLightsIntensity = register(new Uniform("u_spotLights[0].intensity"));
    private int firstSptLightsCutoffAngle = register(new Uniform("u_spotLights[0].cutoffAngle"));
    private int firstSptLightsExponent = register(new Uniform("u_spotLights[0].exponent"));
    private int secondSptLightsColor = register(new Uniform("u_spotLights[1].color"));

    private int dirLightsLoc;
    private int dirLightsColorOffset;
    private int dirLightsDirectionOffset;
    private int dirLightsIntensityOffset;
    private int dirLightsSize;

    private int ambientLightColorLoc;

    private int pntLightsLoc;
    private int pntLightsColorOffset;
    private int pntLightsPositionOffset;
    private int pntLightsIntensityOffset;
    private int pntLightsRadiusOffset;
    private int pntLightsSize;

    private int sptLightsLoc;
    private int sptLightsColorOffset;
    private int sptLightsPositionOffset;
    private int sptLightsDirectionOffset;
    private int sptLightsIntensityOffset;
    private int sptLightsCutoffAngleOffset;
    private int sptLightsExponentOffset;
    private int sptLightsSize;

    private ShaderProgram shaderProgram;
    private Renderable renderable;

    private Matrix4 idtMatrix;

    public LightingShader(Renderable renderable, boolean skinned) {
        this.renderable = renderable;
        this.skinned = skinned;

        String prefix = ShaderUtils.createPrefix(renderable, skinned);

        String vert =
                prefix +
                        Gdx.files.internal("shaders/" + "lighting_shader" + ".vert")
                                .readString();

        String frag =
                prefix +
                        Gdx.files.internal("shaders/" + "lighting_shader" + ".frag")
                                .readString();

        shaderProgram = new ShaderProgram(vert, frag);

        if (!shaderProgram.isCompiled()) throw new GdxRuntimeException(shaderProgram.getLog());
        Gdx.app.log("Shader Log", shaderProgram.getLog());

        register("u_modelViewProjectionMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.combined);
            }
        });

        register("u_modelMatrix", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, renderable.worldTransform);
            }
        });

        register("u_viewMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.view);
            }
        });

        register("u_normalMatrix", new LocalSetter() {
            private final Matrix3 tmpM = new Matrix3();

            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, tmpM.set(renderable.worldTransform).inv().transpose());
            }
        });

        /*register("u_texScale", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute diffuseAttr = (TextureAttribute) combinedAttributes.get(TextureAttribute.Diffuse);
                shader.set(inputID, new Vector2(diffuseAttr.scaleU, diffuseAttr.scaleV));
            }
        });

        register("u_texOffset", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute diffuseAttr = (TextureAttribute) combinedAttributes.get(TextureAttribute.Diffuse);
                shader.set(inputID, new Vector2(diffuseAttr.offsetU, diffuseAttr.offsetV));
            }
        });*/

        register("u_diffuse", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute textureAttribute = (TextureAttribute) combinedAttributes.get(TextureAttribute.Diffuse);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });
    }

    @Override
    public void init() {
        idtMatrix = new Matrix4();
        init(shaderProgram, renderable);

        // Directional lights
        dirLightsLoc = loc(firstDirLightsColor);

        dirLightsColorOffset = loc(firstDirLightsColor) - dirLightsLoc;
        dirLightsDirectionOffset = loc(firstDirLightsDirection) - dirLightsLoc;
        dirLightsIntensityOffset = loc(firstDirLightsIntensity) - dirLightsLoc;

        dirLightsSize = loc(secondDirLightsColor) - dirLightsLoc;
        if (dirLightsSize < 0) dirLightsSize = 0;

        // Ambient light
        ambientLightColorLoc = loc(ambientLightColor);

        // Point lights
        pntLightsLoc = loc(firstPntLightsColor);

        pntLightsColorOffset = loc(firstPntLightsColor) - pntLightsLoc;
        pntLightsPositionOffset = loc(firstPntLightsPosition) - pntLightsLoc;
        pntLightsIntensityOffset = loc(firstPntLightsIntensity) - pntLightsLoc;
        pntLightsRadiusOffset = loc(firstPntLightsRadius) - pntLightsLoc;

        pntLightsSize = loc(secondPntLightsColor) - pntLightsLoc;
        if (pntLightsSize < 0) pntLightsSize = 0;

        // Spot lights
        sptLightsLoc = loc(firstSptLightsColor);

        sptLightsColorOffset = loc(firstSptLightsColor) - sptLightsLoc;
        sptLightsPositionOffset = loc(firstSptLightsPosition) - sptLightsLoc;
        sptLightsDirectionOffset = loc(firstSptLightsDirection) - sptLightsLoc;
        sptLightsIntensityOffset = loc(firstSptLightsIntensity) - sptLightsLoc;
        sptLightsCutoffAngleOffset = loc(firstSptLightsCutoffAngle) - sptLightsLoc;
        sptLightsExponentOffset = loc(firstSptLightsExponent) - sptLightsLoc;

        sptLightsSize = loc(secondSptLightsColor) - sptLightsLoc;
        if (sptLightsSize < 0) sptLightsSize = 0;
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        bindLights(renderable, combinedAttributes);
        if (skinned) updateBones("u_bones", renderable);

        super.render(renderable, combinedAttributes);
    }

    @Override
    public void end() {
        super.end();
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void bindLights(Renderable renderable, Attributes attributes) {
        Environment environment = renderable.environment;

        ColorAttribute ambientLightAttribute = (ColorAttribute) attributes.get(ColorAttribute.AmbientLight);
        Color ambientLightColor;

        if (ambientLightAttribute != null) {
            ambientLightColor = ambientLightAttribute.color;

            shaderProgram.setUniformf(ambientLightColorLoc,
                    ambientLightColor.r,
                    ambientLightColor.g,
                    ambientLightColor.b);
        }

        // Get directional lights
        DirectionalLightsAttribute directionalLightsAttribute = (DirectionalLightsAttribute) environment.get(DirectionalLightsAttribute.Type);

        if (directionalLightsAttribute != null) {
            Array<DirectionalLight> directionalLights = directionalLightsAttribute.lights;

            for (int i = 0; i < directionalLights.size; i++) {
                IntensityDirectionalLight directionalLight = (IntensityDirectionalLight) directionalLights.get(i);

                int address = dirLightsLoc + i * dirLightsSize;

                shaderProgram.setUniformf(address + dirLightsColorOffset,
                        directionalLight.color.r,
                        directionalLight.color.g,
                        directionalLight.color.b);

                shaderProgram.setUniformf(address + dirLightsDirectionOffset,
                        directionalLight.direction.x,
                        directionalLight.direction.y,
                        directionalLight.direction.z);

                shaderProgram.setUniformf(address + dirLightsIntensityOffset, directionalLight.intensity);

                if (dirLightsSize <= 0) break;
            }
        }

        // Get point lights
        PointLightsAttribute pointLightsAttribute = (PointLightsAttribute) environment.get(PointLightsAttribute.Type);

        if (pointLightsAttribute != null) {
            Array<PointLight> pointLights = pointLightsAttribute.lights;

            for (int i = 0; i < pointLights.size; i++) {
                RadiusPointLight radiusPointLight = (RadiusPointLight) pointLights.get(i);

                int address = pntLightsLoc + i * pntLightsSize;

                shaderProgram.setUniformf(address + pntLightsColorOffset,
                        radiusPointLight.color.r,
                        radiusPointLight.color.g,
                        radiusPointLight.color.b);

                shaderProgram.setUniformf(address + pntLightsPositionOffset,
                        radiusPointLight.position.x,
                        radiusPointLight.position.y,
                        radiusPointLight.position.z);

                shaderProgram.setUniformf(address + pntLightsIntensityOffset, radiusPointLight.intensity);
                shaderProgram.setUniformf(address + pntLightsRadiusOffset, radiusPointLight.radius);

                if (pntLightsSize <= 0) break;
            }
        }

        // Get spot lights
        SpotLightsAttribute spotLightsAttribute = (SpotLightsAttribute) environment.get(SpotLightsAttribute.Type);

        if (spotLightsAttribute != null) {
            Array<SpotLight> spotLights = spotLightsAttribute.lights;

            for (int i = 0; i < spotLights.size; i++) {
                SpotLight spotLight = spotLights.get(i);

                int address = sptLightsLoc + i * sptLightsSize;

                shaderProgram.setUniformf(address + sptLightsColorOffset,
                        spotLight.color.r,
                        spotLight.color.g,
                        spotLight.color.b);

                shaderProgram.setUniformf(address + sptLightsPositionOffset,
                        spotLight.position.x,
                        spotLight.position.y,
                        spotLight.position.z);

                shaderProgram.setUniformf(address + sptLightsDirectionOffset,
                        spotLight.direction.x,
                        spotLight.direction.y,
                        spotLight.direction.z);

                shaderProgram.setUniformf(address + sptLightsIntensityOffset, spotLight.intensity);
                shaderProgram.setUniformf(address + sptLightsCutoffAngleOffset, spotLight.cutoffAngle);
                shaderProgram.setUniformf(address + sptLightsExponentOffset, spotLight.exponent);

                if (sptLightsSize <= 0) break;
            }
        }
    }

    private void updateBones(String loc, Renderable renderable) {
        if (renderable.bones != null) {
            float[] bones = new float[renderable.bones.length * 16];

            for (int i = 0; i < bones.length; i++) {
                final int idx = i / 16;
                bones[i] = (idx >= renderable.bones.length || renderable.bones[idx] == null) ?
                        idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16];
            }

            shaderProgram.setUniformMatrix4fv(loc, bones, 0, bones.length);
        }
    }
}
