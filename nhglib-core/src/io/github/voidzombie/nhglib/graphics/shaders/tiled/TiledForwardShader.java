package io.github.voidzombie.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.lights.tiled.NhgLight;
import io.github.voidzombie.nhglib.graphics.lights.tiled.NhgLightsAttribute;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.voidzombie.nhglib.utils.data.VectorPool;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class TiledForwardShader extends BaseShader {
    private float bones[];

    private Vector3 vector;
    private Matrix4 matrix;
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

    public TiledForwardShader(Renderable renderable, Environment environment, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;

        String prefix = createPrefix(renderable);

        String vert = prefix + Gdx.files.internal(params.vertexShaderPath).readString();
        String frag = prefix + Gdx.files.internal(params.fragmentShaderPath).readString();

        ShaderProgram.pedantic = false;
        shaderProgram = new ShaderProgram(vert, frag);

        String shaderLog = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            throw new GdxRuntimeException(shaderLog);
        }

        if (!shaderLog.isEmpty()) {
            Nhg.logger.log(this, shaderLog);
        }

        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);
        lights = lightsAttribute.lights;

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

        if (params.albedo) {
            register("u_albedo", new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Albedo);

                    if (textureAttribute != null) {
                        shader.set(inputID, textureAttribute.textureDescription.texture);
                    }
                }
            });
        }

        if (params.metalness) {
            register("u_metalness", new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Metalness);

                    if (textureAttribute != null) {
                        shader.set(inputID, textureAttribute.textureDescription.texture);
                    }
                }
            });
        }

        if (params.roughness) {
            register("u_roughness", new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.Roughness);

                    if (textureAttribute != null) {
                        shader.set(inputID, textureAttribute.textureDescription.texture);
                    }
                }
            });
        }

        if (params.ambientOcclusion) {
            register("u_roughness", new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    PbrTextureAttribute textureAttribute = (PbrTextureAttribute) combinedAttributes.get(PbrTextureAttribute.AmbientOcclusion);

                    if (textureAttribute != null) {
                        shader.set(inputID, textureAttribute.textureDescription.texture);
                    }
                }
            });
        }

        if (params.normal) {
            register("u_normal", new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    TextureAttribute textureAttribute = (TextureAttribute) combinedAttributes.get(TextureAttribute.Normal);

                    if (textureAttribute != null) {
                        shader.set(inputID, textureAttribute.textureDescription.texture);
                    }
                }
            });
        }

        if (params.lit) {
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
        }
    }

    @Override
    public void init() {
        super.init(shaderProgram, renderable);

        if (params.useBones) {
            idtMatrix = new Matrix4();
            bones = new float[0];
        }

        if (params.lit) {
            lightsFrustum = new Array<>();
            lightsToRender = new Array<>();

            for (int i = 0; i < 100; i++) {
                lightsFrustum.add(new IntArray());
            }

            color = new Color();
            matrix = new Matrix4();
            vector = VectorPool.getVector3();

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

        boolean normal = ShaderUtils.hasNormal(instance) == params.normal;
        boolean ambientOcclusion = ShaderUtils.hasAmbientOcclusion(instance) == params.ambientOcclusion;

        boolean bones = ShaderUtils.useBones(instance) == params.useBones;
        boolean lit = ShaderUtils.hasLights(instance.environment) == params.lit;

        return albedo && metalness && roughness && normal && ambientOcclusion && bones && lit;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;

        if (params.lit) {
            frustums.setFrustums(((PerspectiveCamera) camera));

            if (lights != null) {
                for (NhgLight light : lights) {
                    if (camera.frustum.sphereInFrustum(light.position, light.radius) &&
                            camera.position.dst(light.position) < 15f) {
                        lightsToRender.add(light);
                    }
                }
            }

            createLightTexture();
        }

        super.begin(camera, context);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    }

    @Override
    public void render(Renderable renderable) {
        if (params.lit) {
            for (int light = 0; light < lightsToRender.size; light++) {
                NhgLight nhgLight = lightsToRender.get(light);
                String lightUniform = "u_lightsList[" + light + "].";

                shaderProgram.setUniformf(lightUniform + "position", getViewSpacePosition(nhgLight));
                shaderProgram.setUniformf(lightUniform + "direction", nhgLight.direction);
                shaderProgram.setUniformf(lightUniform + "intensity", nhgLight.intensity);
                shaderProgram.setUniformf(lightUniform + "innerAngle", nhgLight.innerAngle);
                shaderProgram.setUniformf(lightUniform + "outerAngle", nhgLight.outerAngle);
            }
        }

        if (params.useBones) {
            updateBones(renderable);
        }

        super.render(renderable);
    }

    @Override
    public void end() {
        if (params.lit) {
            lightsToRender.clear();
        }

        super.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
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
            color.a = light.radius / 255.0f;

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

    private String createPrefix(Renderable renderable) {
        String prefix = "";

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
            prefix += "#define albedoMap\n";
        }

        if (params.metalness) {
            prefix += "#define metalnessMap\n";
        }

        if (params.roughness) {
            prefix += "#define roughnessMap\n";
        }

        if (params.normal) {
            prefix += "#define normalMap\n";
        }

        if (params.ambientOcclusion) {
            prefix += "#define ambientOcclusionMap";
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

    public static class Params {
        boolean albedo;
        boolean metalness;
        boolean roughness;
        boolean normal;
        boolean ambientOcclusion;
        boolean useBones;
        boolean lit;

        String vertexShaderPath;
        String fragmentShaderPath;
    }
}
