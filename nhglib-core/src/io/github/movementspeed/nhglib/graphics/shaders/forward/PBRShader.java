package io.github.movementspeed.nhglib.graphics.shaders.forward;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class PBRShader extends BaseShader {
    protected final int u_dirLights0color = register(new Uniform("u_dirLights[0].color"));
    protected final int u_dirLights0direction = register(new Uniform("u_dirLights[0].direction"));
    protected final int u_dirLights0intensity = register(new Uniform("u_dirLights[0].intensity"));
    protected final int u_dirLights1color = register(new Uniform("u_dirLights[1].color"));

    protected final int u_pointLights0color = register(new Uniform("u_pointLights[0].color"));
    protected final int u_pointLights0position = register(new Uniform("u_pointLights[0].position"));
    protected final int u_pointLights0intensity = register(new Uniform("u_pointLights[0].intensity"));
    protected final int u_pointLights0radius = register(new Uniform("u_pointLights[0].radius"));
    protected final int u_pointLights1color = register(new Uniform("u_pointLights[1].color"));

    protected final int u_spotLights0color = register(new Uniform("u_spotLights[0].color"));
    protected final int u_spotLights0position = register(new Uniform("u_spotLights[0].position"));
    protected final int u_spotLights0direction = register(new Uniform("u_spotLights[0].direction"));
    protected final int u_spotLights0intensity = register(new Uniform("u_spotLights[0].intensity"));
    protected final int u_spotLights0innerAngle = register(new Uniform("u_spotLights[0].innerAngle"));
    protected final int u_spotLights0outerAngle = register(new Uniform("u_spotLights[0].outerAngle"));
    protected final int u_spotLights1color = register(new Uniform("u_spotLights[1].color"));

    protected int dirLightsLoc;
    protected int dirLightsColorOffset;
    protected int dirLightsDirectionOffset;
    protected int dirLightsIntensityOffset;
    protected int dirLightsSize;

    protected int pointLightsLoc;
    protected int pointLightsColorOffset;
    protected int pointLightsPositionOffset;
    protected int pointLightsIntensityOffset;
    protected int pointLightsRadiusOffset;
    protected int pointLightsSize;

    protected int spotLightsLoc;
    protected int spotLightsColorOffset;
    protected int spotLightsPositionOffset;
    protected int spotLightsDirectionOffset;
    protected int spotLightsIntensityOffset;
    protected int spotLightsInnerAngleOffset;
    protected int spotLightsOuterAngleOffset;
    protected int spotLightsSize;

    private boolean lightsSet;

    private int maxBonesLength = Integer.MIN_VALUE;
    private int bonesIID;
    private int bonesLoc;
    private float bones[];

    private Matrix4 idtMatrix;

    private Params params;
    private Renderable renderable;
    private Environment environment;
    private ShaderProgram shaderProgram;

    private NhgLightsAttribute lightsAttribute;

    protected final NhgLight directionalLights[];
    protected final NhgLight pointLights[];
    protected final NhgLight spotLights[];

    public PBRShader(Renderable renderable, Environment environment, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;

        this.directionalLights = new NhgLight[params.lit ? 2 : 0];
        for (int i = 0; i < directionalLights.length; i++) {
            directionalLights[i] = new NhgLight();
        }

        this.pointLights = new NhgLight[params.lit ? 5 : 0];
        for (int i = 0; i < pointLights.length; i++) {
            pointLights[i] = new NhgLight();
        }

        this.spotLights = new NhgLight[0];
        for (int i = 0; i < spotLights.length; i++) {
            spotLights[i] = new NhgLight();
        }

        String prefix = createPrefix(renderable);
        String folder = "shaders/gl3/";

        switch (Nhg.version) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        String vert = prefix + Gdx.files.internal(folder + "tf_pbr_shader.vert").readString();
        String frag = prefix + Gdx.files.internal(folder + "pbr_shader.frag").readString();

        ShaderProgram.pedantic = true;
        shaderProgram = new ShaderProgram(vert, frag);

        String shaderLog = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            throw new GdxRuntimeException(shaderLog);
        }

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

        dirLightsLoc = loc(u_dirLights0color);
        dirLightsColorOffset = loc(u_dirLights0color) - dirLightsLoc;
        dirLightsDirectionOffset = loc(u_dirLights0direction) - dirLightsLoc;
        dirLightsIntensityOffset = loc(u_dirLights0intensity) - dirLightsLoc;
        dirLightsSize = loc(u_dirLights1color) - dirLightsLoc;
        if (dirLightsSize < 0) dirLightsSize = 0;

        pointLightsLoc = loc(u_pointLights0color);
        pointLightsColorOffset = loc(u_pointLights0color) - pointLightsLoc;
        pointLightsPositionOffset = loc(u_pointLights0position) - pointLightsLoc;
        pointLightsIntensityOffset = has(u_pointLights0intensity) ? loc(u_pointLights0intensity) - pointLightsLoc : -1;
        pointLightsRadiusOffset = has(u_pointLights0radius) ? loc(u_pointLights0radius) - pointLightsLoc : -1;
        pointLightsSize = loc(u_pointLights1color) - pointLightsLoc;
        if (pointLightsSize < 0) pointLightsSize = 0;

        spotLightsLoc = loc(u_spotLights0color);
        spotLightsColorOffset = loc(u_spotLights0color) - spotLightsLoc;
        spotLightsPositionOffset = loc(u_spotLights0position) - spotLightsLoc;
        spotLightsDirectionOffset = loc(u_spotLights0direction) - spotLightsLoc;
        spotLightsIntensityOffset = has(u_spotLights0intensity) ? loc(u_spotLights0intensity) - spotLightsLoc : -1;
        spotLightsInnerAngleOffset = loc(u_spotLights0innerAngle) - spotLightsLoc;
        spotLightsOuterAngleOffset = loc(u_spotLights0outerAngle) - spotLightsLoc;
        spotLightsSize = loc(u_spotLights1color) - spotLightsLoc;
        if (spotLightsSize < 0) spotLightsSize = 0;
    }


    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        boolean albedo = ShaderUtils.hasAlbedo(instance) == params.albedo;
        boolean metalness = ShaderUtils.hasMetalness(instance) == params.metalness;
        boolean roughness = ShaderUtils.hasRoughness(instance) == params.roughness;
        boolean normal = ShaderUtils.hasPbrNormal(instance) == params.normal;
        boolean ambientOcclusion = ShaderUtils.hasAmbientOcclusion(instance) == params.ambientOcclusion;
        boolean bones = ShaderUtils.useBones(instance) == params.useBones;
        boolean lit = ShaderUtils.hasLights(instance.environment) == params.lit;
        boolean gammaCorrection = ShaderUtils.useGammaCorrection(instance.environment) == params.gammaCorrection;
        boolean imageBasedLighting = ShaderUtils.useImageBasedLighting(instance.environment) == params.imageBasedLighting;

        return albedo && metalness && roughness && normal &&
                ambientOcclusion && bones && lit && gammaCorrection && imageBasedLighting;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;

        context.setCullFace(GL20.GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);

        super.begin(camera, context);

        for (final NhgLight dirLight : directionalLights) {
            dirLight.color.set(0, 0, 0, 1);
            dirLight.direction.set(0, -1, 0);
        }

        for (final NhgLight pointLight : pointLights) {
            pointLight.color.set(0, 0, 0, 1);
            pointLight.position.set(0, 0, 0);
            pointLight.intensity = 0;
        }

        for (final NhgLight spotLight : spotLights) {
            spotLight.color.set(0, 0, 0, 1);
            spotLight.position.set(0, 0, 0);
            spotLight.direction.set(0, -1, 0);
            spotLight.intensity = 0;
            spotLight.innerAngle = 0;
            spotLight.outerAngle = 0;
        }

        lightsSet = false;
    }

    @Override
    public void render(Renderable renderable) {
        if (params.lit) {
            bindLights(renderable);
        }

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

    private String createPrefix(Renderable renderable) {
        String prefix = "";

        switch (Nhg.version) {
            case VERSION_3:
                prefix = "#version 300 es\n";
                break;
        }

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
            int directionalLights = 0;
            int pointLights = 0;
            int spotLights = 0;

            for (NhgLight light : lightsAttribute.lights) {
                switch (light.type) {
                    case DIRECTIONAL_LIGHT:
                        directionalLights++;
                        break;

                    case SPOT_LIGHT:
                        spotLights++;
                        break;

                    case POINT_LIGHT:
                        pointLights++;
                        break;
                }
            }

            if (directionalLights > 0) {
                prefix += "#define numDirectionalLights " + directionalLights + "\n";
            }

            if (pointLights > 0) {
                prefix += "#define numPointLights " + pointLights + "\n";
            }

            if (spotLights > 0) {
                prefix += "#define numSpotLights " + spotLights + "\n";
            }
        }

        String renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase();

        if (renderer.contains("MALI")) {
            prefix += "#define GPU_MALI\n";
        } else if (renderer.contains("ADRENO")) {
            prefix += "#define GPU_ADRENO\n";
        }

        return prefix;
    }

    protected void bindLights (final Renderable renderable) {
        lightsAttribute = (NhgLightsAttribute) renderable.environment.get(NhgLightsAttribute.Type);
        Array<NhgLight> lights = lightsAttribute.lights;

        final Array<NhgLight> dirs = new Array<>();
        final Array<NhgLight> points = new Array<>();
        final Array<NhgLight> spots = new Array<>();

        for (NhgLight light : lights) {
            switch (light.type) {
                case DIRECTIONAL_LIGHT:
                    dirs.add(light);
                    break;

                case SPOT_LIGHT:
                    spots.add(light);
                    break;

                case POINT_LIGHT:
                    points.add(light);
                    break;
            }
        }

        if (dirLightsLoc >= 0) {
            for (int i = 0; i < directionalLights.length; i++) {
                if (i >= dirs.size) {
                    if (lightsSet &&
                            directionalLights[i].color.r == 0f &&
                            directionalLights[i].color.g == 0f &&
                            directionalLights[i].color.b == 0f) {
                        continue;
                    }

                    directionalLights[i].color.set(0, 0, 0, 1);
                } else if (lightsSet && directionalLights[i].equals(dirs.get(i))) {
                    continue;
                } else {
                    directionalLights[i].set(dirs.get(i));
                }

                int idx = dirLightsLoc + i * dirLightsSize;

                program.setUniformf(idx + dirLightsColorOffset,
                        directionalLights[i].color.r,
                        directionalLights[i].color.g,
                        directionalLights[i].color.b);

                program.setUniformf(idx + dirLightsDirectionOffset,
                        directionalLights[i].direction.x,
                        directionalLights[i].direction.y,
                        directionalLights[i].direction.z);

                if (dirLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + dirLightsIntensityOffset, directionalLights[i].intensity);
                }

                if (dirLightsSize <= 0) break;
            }
        }

        if (pointLightsLoc >= 0) {
            for (int i = 0; i < pointLights.length; i++) {
                if (i >= points.size) {
                    if (lightsSet && pointLights[i].intensity == 0f) {
                        continue;
                    }

                    pointLights[i].intensity = 0f;
                } else if (lightsSet && pointLights[i].equals(points.get(i))) {
                    continue;
                } else {
                    pointLights[i].set(points.get(i));
                }

                int idx = pointLightsLoc + i * pointLightsSize;

                program.setUniformf(idx + pointLightsColorOffset,
                        pointLights[i].color.r * pointLights[i].intensity,
                        pointLights[i].color.g * pointLights[i].intensity,
                        pointLights[i].color.b * pointLights[i].intensity);

                program.setUniformf(idx + pointLightsPositionOffset,
                        pointLights[i].position.x,
                        pointLights[i].position.y,
                        pointLights[i].position.z);

                if (pointLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + pointLightsIntensityOffset, pointLights[i].intensity);
                }

                if (pointLightsRadiusOffset >= 0) {
                    program.setUniformf(idx + pointLightsRadiusOffset, pointLights[i].radius);
                }

                if (pointLightsSize <= 0) {
                    break;
                }
            }
        }

        if (spotLightsLoc >= 0) {
            for (int i = 0; i < spotLights.length; i++) {
                if (i >= spots.size) {
                    if (lightsSet && spotLights[i].intensity == 0f) {
                        continue;
                    }

                    spotLights[i].intensity = 0f;
                } else if (lightsSet && spotLights[i].equals(spots.get(i))) {
                    continue;
                } else {
                    spotLights[i].set(spots.get(i));
                }

                int idx = spotLightsLoc + i * spotLightsSize;

                program.setUniformf(idx + spotLightsColorOffset,
                        spotLights[i].color.r * spotLights[i].intensity,
                        spotLights[i].color.g * spotLights[i].intensity,
                        spotLights[i].color.b * spotLights[i].intensity);

                program.setUniformf(idx + spotLightsPositionOffset, spotLights[i].position);
                program.setUniformf(idx + spotLightsDirectionOffset, spotLights[i].direction);
                program.setUniformf(idx + spotLightsInnerAngleOffset, spotLights[i].innerAngle);
                program.setUniformf(idx + spotLightsOuterAngleOffset, spotLights[i].outerAngle);

                if (spotLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + spotLightsIntensityOffset, spotLights[i].intensity);
                }

                if (spotLightsSize <= 0) {
                    break;
                }
            }
        }

        lightsSet = true;
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
