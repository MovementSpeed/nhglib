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
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.enums.LightType;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute;
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

    private IntArray lightTypes;
    private FloatArray lightIntensities;
    private FloatArray lightInnerAngles;
    private FloatArray lightOuterAngles;
    private FloatArray lightPositions;
    private FloatArray lightDirections;

    private Array<IntArray> lightsFrustum;
    private Array<NhgLight> lights;

    public PBRShader(Renderable renderable, Environment environment, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;

        String prefix = createPrefix(renderable);
        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        String vert = prefix + Gdx.files.internal(folder + "tf_pbr_shader.vert").readString();
        String frag = prefix + Gdx.files.internal(folder + "tf_pbr_shader.frag").readString();

        ShaderProgram.pedantic = false;
        shaderProgram = new ShaderProgram(vert, frag);

        String shaderLog = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            throw new GdxRuntimeException(shaderLog);
        }

        register("u_mvpMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.combined);
            }
        });

        register("u_viewMatrix", new GlobalSetter() {
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

        register("u_graphicsWidth", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, RenderingSystem.renderWidth);
            }
        });

        register("u_graphicsHeight", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, RenderingSystem.renderHeight);
            }
        });

        register("u_albedo", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Albedo);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_metalness", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Metalness);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_roughness", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Roughness);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_normal", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Normal);

                if (textureAttribute != null) {
                    shader.set(inputID, textureAttribute.textureDescription.texture);
                }
            }
        });

        register("u_ambientOcclusion", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.AmbientOcclusion);

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

        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);

        if (lightsAttribute != null) {
            lights = lightsAttribute.lights;
        } else {
            lights = new Array<>();
        }

        lightTypes = new IntArray(new int[lights.size]);
        lightIntensities = new FloatArray(new float[lights.size]);
        lightInnerAngles = new FloatArray(new float[lights.size]);
        lightOuterAngles = new FloatArray(new float[lights.size]);
        lightPositions = new FloatArray(new float[lights.size * 3]);
        lightDirections = new FloatArray(new float[lights.size * 3]);

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);

            lightTypes.set(i, l.type.ordinal());
            lightIntensities.set(i, l.intensity);
            lightInnerAngles.set(i, l.innerAngle);
            lightOuterAngles.set(i, l.outerAngle);
        }

        shaderProgram.begin();
        for (int i = 0; i < lightTypes.size; i++) {
            shaderProgram.setUniformi("u_lightTypes[" + i + "]", lightTypes.get(i));
        }

        shaderProgram.setUniform1fv("u_lightIntensities", lightIntensities.items, 0, lights.size);
        shaderProgram.setUniform1fv("u_lightInnerAngles", lightInnerAngles.items, 0, lights.size);
        shaderProgram.setUniform1fv("u_lightOuterAngles", lightOuterAngles.items, 0, lights.size);
        shaderProgram.end();

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

        context.setCullFace(GL20.GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);

        lightGrid.setFrustums(((PerspectiveCamera) camera));
        makeLightTexture();

        super.begin(camera, context);
        program.setUniform3fv("u_lightPositions", getLightPositions(), 0, lights.size * 3);
        program.setUniform3fv("u_lightDirections", getLightDirections(), 0, lights.size * 3);
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
            lightGrid.checkFrustums(l.position, l.radius, lightsFrustum, i);
        }

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);
            color.set(l.color.r, l.color.g, l.color.b, l.radius / 255);
            lightInfoPixmap.setColor(color);
            lightInfoPixmap.drawPixel(0, i);
        }

        lightInfoTexture.draw(lightInfoPixmap, 0, 0);

        for (int row = 0; row < 100; row++) {
            int col = 0;
            float r = lightsFrustum.get(row).size;

            color.set(r / 255, 0, 0, 0);
            lightPixmap.setColor(color);
            lightPixmap.drawPixel(col, row);

            col++;

            for (int i = 0; i < lightsFrustum.get(row).size; i++) {
                int j = lightsFrustum.get(row).get(i);

                color.set(((float) j) / 255, 0, 0, 0);
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

    private float[] getLightPositions() {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            vec1.set(lights.get(k).position);
            vec1.mul(camera.view);

            lightPositions.set(i++, vec1.x);
            lightPositions.set(i++, vec1.y);
            lightPositions.set(i++, vec1.z);
        }

        return lightPositions.items;
    }

    private float[] getLightDirections() {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);

            if (light.type != LightType.POINT_LIGHT) {
                vec1.set(light.direction)
                        .rot(camera.view);

                lightDirections.set(i++, vec1.x);
                lightDirections.set(i++, vec1.y);
                lightDirections.set(i++, vec1.z);
            }
        }

        return lightDirections.items;
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