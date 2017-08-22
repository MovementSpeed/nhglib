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
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.movementspeed.nhglib.utils.data.MatrixPool;
import io.github.movementspeed.nhglib.utils.data.VectorPool;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class PBRShader extends BaseShader {
    public static float lightRenderDistance = 15f;

    private float bones[];

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
    private SmallFrustums frustums;
    private ShaderProgram shaderProgram;

    private Array<IntArray> lightsFrustum;
    private Array<NhgLight> lights;
    private Array<NhgLight> lightsToRender;

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
                shader.set(inputID, Gdx.graphics.getWidth());
            }
        });

        register("u_graphicsHeight", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, Gdx.graphics.getHeight());
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
    }

    @Override
    public void init() {
        super.init(shaderProgram, renderable);

        idtMatrix = new Matrix4();
        bones = new float[0];

        lightsFrustum = new Array<>();
        lightsToRender = new Array<>();

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

        frustums = new SmallFrustums(10, 10);
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
        frustums.setFrustums(((PerspectiveCamera) camera));

        cullLights();
        createLightTexture();

        super.begin(camera, context);
        context.setCullFace(GL20.GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setDepthMask(true);
    }

    @Override
    public void render(Renderable renderable) {
        for (int light = 0; light < lightsToRender.size; light++) {
            NhgLight nhgLight = lightsToRender.get(light);
            String lightUniform = "u_lightsList[" + light + "].";

            Vector3 viewSpacePosition = getViewSpacePosition(nhgLight);
            Vector3 viewSpaceDirection = getViewSpaceDirection(nhgLight);

            shaderProgram.setUniformi(lightUniform + "type", nhgLight.type.ordinal());
            shaderProgram.setUniformf(lightUniform + "position", viewSpacePosition);
            shaderProgram.setUniformf(lightUniform + "direction", viewSpaceDirection);
            shaderProgram.setUniformf(lightUniform + "intensity", nhgLight.intensity);
            shaderProgram.setUniformf(lightUniform + "innerAngle", nhgLight.innerAngle);
            shaderProgram.setUniformf(lightUniform + "outerAngle", nhgLight.outerAngle);

            VectorPool.freeVector3(viewSpacePosition, viewSpaceDirection);
        }

        updateBones(renderable);
        super.render(renderable);
    }

    @Override
    public void end() {
        super.end();

        lightsToRender.clear();
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
    }

    private void createLightTexture() {
        int i;

        for (i = 0; i < 100; i++) {
            lightsFrustum.get(i).clear();
        }

        for (i = 0; i < lightsToRender.size; i++) {
            NhgLight light = lightsToRender.get(i);

            frustums.checkFrustums(light.position, light.radius, lightsFrustum, i);
            color.set(light.color);
            color.a = light.radius / 255f;

            lightInfoPixmap.setColor(color);
            lightInfoPixmap.drawPixel(0, i);
        }

        lightInfoTexture.draw(lightInfoPixmap, 0, 0);

        for (int row = 0; row < 100; row++) {
            int column = 0;
            float r = lightsFrustum.get(row).size;

            color.set(r / 255.0f, 0, 0, 0);

            lightPixmap.setColor(color);
            lightPixmap.drawPixel(column, row);

            column++;

            for (int k = 0; k < lightsFrustum.get(row).size; k++) {
                int j = (lightsFrustum.get(row).get(k));

                color.set(((float) j) / 255.0f, 0, 0, 0);

                lightPixmap.setColor(color);
                lightPixmap.drawPixel(column, row);

                column++;
            }
        }

        lightTexture.draw(lightPixmap, 0, 0);
    }

    private void updateBones(Renderable renderable) {
        if (renderable.bones != null) {
            bones = new float[renderable.bones.length * 16];

            for (int i = 0; i < bones.length; i++) {
                final int idx = i / 16;
                bones[i] = (idx >= renderable.bones.length || renderable.bones[idx] == null) ?
                        idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16];
            }

            shaderProgram.setUniformMatrix4fv("u_bones", bones, 0, bones.length);
        }
    }

    private void cullPointLight(NhgLight light) {
        if (camera.frustum.sphereInFrustum(light.position, light.radius) &&
                camera.position.dst(light.position) < lightRenderDistance) {
            lightsToRender.add(light);
        }
    }

    private void cullSpotLight(NhgLight light) {
        Matrix4 a = MatrixPool.getMatrix4();
        a.setToTranslation(light.position);

        Matrix4 b = MatrixPool.getMatrix4();
        b.set(a).translate(new Vector3(light.direction).scl(light.radius));

        Vector3 p1 = VectorPool.getVector3();
        Vector3 p2 = VectorPool.getVector3();
        Vector3 m = VectorPool.getVector3();

        a.getTranslation(p1);
        b.getTranslation(p2);
        m.set(p1).add(p2).scl(0.5f);

        float radius = p1.dst(p2) * 0.5f;

        if (camera.frustum.sphereInFrustum(m, radius)) {
            lightsToRender.add(light);
        }

        VectorPool.freeVector3(p1, p2, m);
        MatrixPool.freeMatrix4(a, b);
    }

    private void cullLights() {
        if (lights != null) {
            for (NhgLight light : lights) {
                switch (light.type) {
                    case POINT_LIGHT:
                        cullPointLight(light);
                        break;

                    case SPOT_LIGHT:
                        cullSpotLight(light);
                        break;

                    case DIRECTIONAL_LIGHT:
                        lightsToRender.add(light);
                        break;
                }
            }
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

        return prefix;
    }

    private Vector3 getViewSpacePosition(NhgLight light) {
        Vector3 position = VectorPool.getVector3();
        position.set(light.position)
                .mul(camera.view);

        return position;
    }

    private Vector3 getViewSpaceDirection(NhgLight light) {
        Vector3 direction = VectorPool.getVector3();
        direction.set(light.direction)
                .rot(camera.view);

        return direction;
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
