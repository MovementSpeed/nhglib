package io.github.movementspeed.nhglib.graphics.shaders.tiledForward;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.enums.LightType;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class PBRShader extends BaseShader {
    private int maxBonesLength = Integer.MIN_VALUE;
    private int bonesIID;
    private int bonesLoc;
    private float bones[];

    private Vector3 vec1 = new Vector3();
    private Matrix4 idtMatrix;

    private Color color;

    private Pixmap lightPixmap;
    private Pixmap lightInfoPixmap;

    private Texture lightTexture;
    private Texture lightInfoTexture;

    private Camera camera;
    private Params params;
    private Renderable renderable;
    private Environment environment;
    private LightGrid lightGrid;
    private ShaderProgram shaderProgram;

    private NhgLightsAttribute lightsAttribute;

    private Array<IntArray> lightsFrustum;
    private Array<NhgLight> lights;

    public PBRShader(Renderable renderable, Environment environment, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;

        String prefix = createPrefix(renderable);

        String vert = prefix + Gdx.files.internal("shaders/tf_pbr_shader.vert").readString();
        String frag = prefix + Gdx.files.internal("shaders/tf_pbr_shader.frag").readString();

        ShaderProgram.pedantic = false;
        shaderProgram = new ShaderProgram(vert, frag);

        String shaderLog = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            throw new GdxRuntimeException(shaderLog);
        }

        lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        lights = lightsAttribute.lights;

        register("u_lights", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, lightTexture);
            }
        });

        register("u_lightInfo", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, lightInfoTexture);
            }
        });

        register("u_mvpMatrix", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.combined);
            }
        });

        register("u_viewMatrix", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.view);
            }
        });

        register("u_modelMatrix", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, renderable.worldTransform);
            }
        });

        register("u_graphicsWidth", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, RenderingSystem.renderWidth);
            }
        });

        register("u_graphicsHeight", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, RenderingSystem.renderHeight);
            }
        });

        register("u_albedo", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Albedo);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_metalness", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Metalness);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_roughness", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Roughness);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_normal", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Normal);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_ambientOcclusion", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.AmbientOcclusion);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_irradiance", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                IBLAttribute attribute = (IBLAttribute) combinedAttributes.get(IBLAttribute.IrradianceType);

                if (attribute != null) {
                    shader.set(inputID, attribute.textureDescription.texture);
                }
            }
        });

        register("u_prefilter", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                IBLAttribute attribute = (IBLAttribute) combinedAttributes.get(IBLAttribute.PrefilterType);

                if (attribute != null) {
                    shader.set(inputID, attribute.textureDescription.texture);
                }
            }
        });

        register("u_brdf", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                IBLAttribute attribute = (IBLAttribute) combinedAttributes.get(IBLAttribute.BrdfType);

                if (attribute != null) {
                    shader.set(inputID, attribute.textureDescription.texture);
                }
            }
        });

        bonesIID = register("u_bones", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if (renderable.bones != null) {
                    int renderableBonesLength = renderable.bones.length * 16;

                    if (renderableBonesLength > maxBonesLength) {
                        maxBonesLength = renderableBonesLength;
                        bones = new float[renderableBonesLength];
                    }

                    for (int i = 0; i < renderableBonesLength; i++) {
                        final int idx = i / 16;
                        bones[i] = (idx >= renderable.bones.length || renderable.bones[idx] == null) ?
                                idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16];
                    }

                    shaderProgram.setUniformMatrix4fv(bonesLoc, bones, 0, renderableBonesLength);
                }
            }
        });
    }

    @Override
    public void init() {
        super.init(shaderProgram, renderable);

        idtMatrix = new Matrix4();
        bones = new float[0];
        bonesLoc = loc(bonesIID);

        lightsFrustum = new Array<>();

        for (int i = 0; i < 100; i++) {
            lightsFrustum.add(new IntArray());
        }

        color = new Color();

        lightTexture = new Texture(64, 128, Pixmap.Format.RGBA8888);
        lightTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        lightInfoTexture = new Texture(1, 128, Pixmap.Format.RGBA8888);
        lightInfoTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        lightPixmap = new Pixmap(64, 128, Pixmap.Format.RGBA8888);
        lightPixmap.setBlending(Pixmap.Blending.None);

        lightInfoPixmap = new Pixmap(1, 128, Pixmap.Format.RGBA8888);
        lightInfoPixmap.setBlending(Pixmap.Blending.None);

        lightGrid = new LightGrid(10, 10);
    }


    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        boolean diffuse = ShaderUtils.hasAlbedo(instance) == params.albedo;
        boolean metalness = ShaderUtils.hasMetalness(instance) == params.metalness;
        boolean roughness = ShaderUtils.hasRoughness(instance) == params.roughness;
        boolean normal = ShaderUtils.hasPbrNormal(instance) == params.normal;
        boolean ambientOcclusion = ShaderUtils.hasAmbientOcclusion(instance) == params.ambientOcclusion;
        boolean bones = ShaderUtils.useBones(instance) == params.useBones;
        boolean lit = ShaderUtils.hasLights(instance.environment) == params.lit;
        boolean gammaCorrection = ShaderUtils.useGammaCorrection(instance.environment) == params.gammaCorrection;
        boolean imageBasedLighting = ShaderUtils.useImageBasedLighting(instance.environment) == params.imageBasedLighting;

        return diffuse && metalness && roughness && normal && ambientOcclusion && bones &&
                lit && gammaCorrection && imageBasedLighting;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        lights = lightsAttribute.lights;

        float[] floatArray = new float[lights.size];
        float[] float3Array = new float[lights.size * 3];

        context.setCullFace(GL20.GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);

        lightGrid.setFrustums(((PerspectiveCamera) camera));
        makeLightTexture();

        super.begin(camera, context);
        if (shaderProgram.hasUniform("u_lightPositions[0]")) {
            shaderProgram.setUniform3fv("u_lightPositions", getLightPositions(float3Array), 0, lights.size * 3);
        }

        if (shaderProgram.hasUniform("u_lightDirections[0]")) {
            shaderProgram.setUniform3fv("u_lightDirections", getLightDirections(float3Array), 0, lights.size * 3);
        }

        if (shaderProgram.hasUniform("u_lightIntensities[0]")) {
            shaderProgram.setUniform1fv("u_lightIntensities", getLightIntensities(floatArray), 0, lights.size);
        }

        if (shaderProgram.hasUniform("u_lightInnerAngles[0]")) {
            shaderProgram.setUniform1fv("u_lightInnerAngles", getLightInnerAngles(floatArray), 0, lights.size);
        }

        if (shaderProgram.hasUniform("u_lightOuterAngles[0]")) {
            shaderProgram.setUniform1fv("u_lightOuterAngles", getLightOuterAngles(floatArray), 0, lights.size);
        }

        for (int i = 0; i < lights.size; i++) {
            NhgLight light = lights.get(i);
            String lightTypeUniform = "u_lightTypes[" + i + "]";

            if (shaderProgram.hasUniform(lightTypeUniform)) {
                shaderProgram.setUniformi(lightTypeUniform, light.type.ordinal());
            }
        }
    }

    @Override
    public void render(Renderable renderable) {
        super.render(renderable);
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
    }

    private void makeLightTexture() {
        for (int i = 0; i < 100; i++) {
            lightsFrustum.get(i).clear();
        }

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);

            if (l.type != LightType.DIRECTIONAL_LIGHT) {
                lightGrid.checkFrustums(l.position, l.radius, lightsFrustum, i);
            } else {
                for (int j = 0; j < 100; j++) {
                    lightsFrustum.get(j).add(i);
                }
            }
        }

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);

            float r = l.color.r;
            float g = l.color.g;
            float b = l.color.b;
            float a = l.radius / 255f;

            color.set(r, g, b, a);
            lightInfoPixmap.setColor(color);
            lightInfoPixmap.drawPixel(0, i);
        }

        lightInfoTexture.draw(lightInfoPixmap, 0, 0);

        for (int row = 0; row < 100; row++) {
            int col = 0;
            float r = lightsFrustum.get(row).size;
            float r255 = r / 255f;

            color.set(r255, 0, 0, 0);
            lightPixmap.setColor(color);
            lightPixmap.drawPixel(col, row);

            col++;

            for (int i = 0; i < lightsFrustum.get(row).size; i++) {
                int j = lightsFrustum.get(row).get(i);
                float j255 = ((float) j) / 255f;

                color.set(j255, 0, 0, 0);
                lightPixmap.setColor(color);
                lightPixmap.drawPixel(col, row);
                col++;
            }
        }

        lightTexture.draw(lightPixmap, 0, 0);
    }

    private String createPrefix(Renderable renderable) {
        String prefix = "#version 300 es\n";

        if (params.useBones) {
            prefix += "#define numBones " + 12 + "\n";
            final int n = renderable.meshPart.mesh.getVertexAttributes().size();

            for (int i = 0; i < n; i++) {
                final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);

                if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                    prefix += "#define boneWeight" + attr.unit + "Flag\n";
                }
            }
        }

        if (params.albedo) {
            prefix += "#define defAlbedo\n";
        }

        if (params.metalness) {
            prefix += "#define defMetalness\n";
        }

        if (params.roughness) {
            prefix += "#define defRoughness\n";
        }

        if (params.normal) {
            prefix += "#define defNormal\n";
        }

        if (params.ambientOcclusion) {
            prefix += "#define defAmbientOcclusion\n";
        }

        if (params.gammaCorrection) {
            prefix += "#define defGammaCorrection\n";
        }

        if (params.imageBasedLighting) {
            prefix += "#define defImageBasedLighting\n";
        }

        if (params.lit) {
            NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
            prefix += "#define lights " + lightsAttribute.lights.size + "\n";
        }

        String renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase();

        if (renderer.contains("MALI")) {
            prefix += "#define GPU_MALI\n";
        } else if (renderer.contains("ADRENO")) {
            prefix += "#define GPU_ADRENO\n";
        }

        return prefix;
    }

    private float[] getLightPositions(float[] res) {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            vec1.set(lights.get(k).position);
            vec1.mul(camera.view);

            res[i++] = vec1.x;
            res[i++] = vec1.y;
            res[i++] = vec1.z;
        }

        return res;
    }

    private float[] getLightDirections(float[] res) {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);

            if (light.type != LightType.POINT_LIGHT) {
                vec1.set(light.direction)
                        .rot(camera.view);

                res[i++] = vec1.x;
                res[i++] = vec1.y;
                res[i++] = vec1.z;
            }
        }

        return res;
    }

    private float[] getLightIntensities(float[] res) {
        for (int i = 0; i < lights.size; i++) {
            res[i] = lights.get(i).intensity;
        }

        return res;
    }

    private float[] getLightInnerAngles(float[] res) {
        for (int i = 0; i < lights.size; i++) {
            res[i] = lights.get(i).innerAngle;
        }

        return res;
    }

    private float[] getLightOuterAngles(float[] res) {
        for (int i = 0; i < lights.size; i++) {
            res[i] = lights.get(i).outerAngle;
        }

        return res;
    }

    public static class Params {
        boolean useBones;
        boolean albedo;
        boolean metalness;
        boolean roughness;
        boolean normal;
        boolean ambientOcclusion;
        boolean lit;
        boolean gammaCorrection;
        boolean imageBasedLighting;
    }
}
