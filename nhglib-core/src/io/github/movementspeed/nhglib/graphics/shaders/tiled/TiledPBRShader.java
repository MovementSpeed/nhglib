package io.github.movementspeed.nhglib.graphics.shaders.tiled;

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
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.LightGrid;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

import static io.github.movementspeed.nhglib.graphics.lighting.tiled.LightGrid.*;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class TiledPBRShader extends BaseShader {
    private int maxBonesLength = Integer.MIN_VALUE;
    private int bonesIID;
    private int bonesLoc;

    private Vector3 tempVec1 = new Vector3();
    private Matrix4 idtMatrix;

    private Camera camera;
    private Params params;
    private Renderable renderable;
    private Environment environment;
    private LightGrid lightGrid;

    private Pixmap lightPixmap;
    private Texture lightTexture;

    private ShaderProgram shaderProgram;

    private float bones[];

    private float[] countsAndOffsets;
    private float[] posAndRadiuses;
    private float[] colors;

    public TiledPBRShader(Renderable renderable, Environment environment, LightGrid lightGrid, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;
        this.lightGrid = lightGrid;

        String prefix = createPrefix(renderable);
        String folder;

        if (Gdx.graphics.isGL30Available()) {
            folder = "shaders/gl3/";

            switch (Nhg.glVersion) {
                case VERSION_2:
                    folder = "shaders/gl2/";
                    break;
            }
        } else {
            folder = "shaders/gl2/";
        }

        String vert = prefix + Gdx.files.internal(folder + "tiled_pbr_shader.vert").readString();
        String frag = prefix + Gdx.files.internal(folder + "tiled_pbr_shader.frag").readString();

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

        countsAndOffsets = new float[TILES_COUNT * 2];
        posAndRadiuses = new float[MAX_LIGHTS * 4];
        colors = new float[MAX_LIGHTS * 4];

        lightTexture = new Texture(64, 128, Pixmap.Format.RGBA8888);
        lightTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        lightPixmap = new Pixmap(64, 128, Pixmap.Format.RGBA8888);
        lightPixmap.setBlending(Pixmap.Blending.None);
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
        boolean lit = ShaderUtils.hasLights(environment) == params.lit;
        boolean gammaCorrection = ShaderUtils.useGammaCorrection(environment) == params.gammaCorrection;
        boolean imageBasedLighting = ShaderUtils.useImageBasedLighting(environment) == params.imageBasedLighting;

        return diffuse && metalness && roughness && normal && ambientOcclusion && bones &&
                lit && gammaCorrection && imageBasedLighting;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;

        context.setCullFace(GL20.GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

        bindGridBuffers();
        makeLightTexture();

        super.begin(camera, context);
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
        IntArray lightList = lightGrid.getLightList();
        int c = 0;
        c++;
    }

    private void bindGridBuffers() {
        if (lightGrid.getLightListLength() > 0) {
            Array<NhgLight> lights = lightGrid.getViewSpaceLights();

            for (int i = 0; i < lights.size; i += 4) {
                NhgLight light = lights.get(i);

                posAndRadiuses[i] = light.position.x;
                posAndRadiuses[i + 1] = light.position.y;
                posAndRadiuses[i + 2] = light.position.z;
                posAndRadiuses[i + 3] = light.radius;

                colors[i] = light.color.r;
                colors[i + 1] = light.color.g;
                colors[i + 2] = light.color.b;
                colors[i + 3] = 1.0f;
            }

            int counts[] = lightGrid.getCounts();
            int offsets[] = lightGrid.getOffsets();

            for (int i = 0; i < TILES_COUNT; i += 2) {
                countsAndOffsets[i] = counts[i];
                countsAndOffsets[i + 1] = offsets[i];
            }

            program.setUniform2fv("u_countsAndOffsets", countsAndOffsets, 0, TILES_COUNT * 2);
            program.setUniform4fv("u_posAndRadiuses", posAndRadiuses, 0, lights.size * 4);
            program.setUniform4fv("u_colors", colors, 0, lights.size * 4);
        }
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

        prefix += "#define TILES_COUNT " + TILES_COUNT + "\n";
        prefix += "#define MAX_LIGHTS " + MAX_LIGHTS + "\n";
        prefix += "#define TILE_DIM " + TILE_SIZE_XY + "\n";
        prefix += "#define GRID_X " + LIGHT_GRID_DIM_X + "\n";

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