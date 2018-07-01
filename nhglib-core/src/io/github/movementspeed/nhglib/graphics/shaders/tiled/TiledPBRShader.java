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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
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
public class TiledPBRShader extends BaseShader {
    private int gridSize;
    private int maxBonesLength = Integer.MIN_VALUE;

    private int bonesIID;
    private int bonesLoc;
    private int positionsAndRadiusesLoc;
    private int directionsAndIntensitiesLoc;

    private Vector3 vec1;
    private Vector2 vec2;
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

    private int[] lightTypes;
    private float[] lightAngles;
    private float[] lightPositionsAndRadiuses;
    private float[] lightDirectionsAndIntensities;
    private float[] bones;

    private Array<IntArray> lightsFrustum;
    private Array<NhgLight> lights;

    public TiledPBRShader(Renderable renderable, Environment environment, Params params) {
        this.renderable = renderable;
        this.environment = environment;
        this.params = params;

        gridSize = 16;

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

        register("u_albedoTiles", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Albedo);

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV));
                }
            }
        });

        register("u_metalnessTiles", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Metalness);

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV));
                }
            }
        });

        register("u_roughnessTiles", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Roughness);

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV));
                }
            }
        });

        register("u_normalTiles", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.Normal);

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV));
                }
            }
        });

        register("u_ambientOcclusionTiles", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) combinedAttributes.get(PBRTextureAttribute.AmbientOcclusion);

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV));
                }
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

        lightTypes = new int[lights.size];
        lightAngles = new float[lights.size * 2];
        lightPositionsAndRadiuses = new float[lights.size * 4];
        lightDirectionsAndIntensities = new float[lights.size * 4];

        setLightTypes();
        setLightAngles();

        shaderProgram.begin();
        for (int i = 0; i < lightTypes.length; i++) {
            shaderProgram.setUniformi("u_lightTypes[" + i + "]", lightTypes[i]);
        }

        shaderProgram.setUniform2fv("u_lightAngles", lightAngles, 0, lights.size * 2);
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

        vec1 = new Vector3();
        vec2 = new Vector2();

        idtMatrix = new Matrix4();
        bones = new float[0];
        bonesLoc = loc(bonesIID);

        positionsAndRadiusesLoc = shaderProgram.fetchUniformLocation("u_lightPositionsAndRadiuses", false);
        directionsAndIntensitiesLoc = shaderProgram.fetchUniformLocation("u_lightDirectionsAndIntensities", false);

        color = new Color();

        lightTexture = new Texture(64, 128, Pixmap.Format.RGBA8888);
        lightTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        lightInfoTexture = new Texture(1, 128, Pixmap.Format.RGBA8888);
        lightInfoTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        lightPixmap = new Pixmap(64, 128, Pixmap.Format.RGBA8888);
        lightPixmap.setBlending(Pixmap.Blending.None);

        lightInfoPixmap = new Pixmap(1, 128, Pixmap.Format.RGBA8888);
        lightInfoPixmap.setBlending(Pixmap.Blending.None);

        lightGrid = new LightGrid(gridSize);
        lightsFrustum = new Array<>();

        for (int i = 0; i < lightGrid.getNumTiles(); i++) {
            lightsFrustum.add(new IntArray());
        }
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

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

        lightGrid.setFrustums(((PerspectiveCamera) camera));

        makeLightTexture();
        setLightPositionsAndRadiusesAndDirections();

        super.begin(camera, context);
        shaderProgram.setUniform4fv(positionsAndRadiusesLoc, lightPositionsAndRadiuses, 0, lights.size * 4);
        shaderProgram.setUniform4fv(directionsAndIntensitiesLoc, lightDirectionsAndIntensities, 0, lights.size * 4);
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
        /* Calculates what lights are affecting what tiles.
         * This is done by dividing the camera frustum. size + size planes,
         * size rows and size columns to serve as the limits for size x size
         * small frustums (Meaning screen is divided into a size x size grid)
         */
        for (int i = 0; i < lightGrid.getNumTiles(); i++) {
            lightsFrustum.get(i).clear();
        }

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);

            if (l.type != LightType.DIRECTIONAL_LIGHT) {
                lightGrid.checkFrustums(l.position, l.radius, lightsFrustum, i);
            } else {
                for (int j = 0; j < lightGrid.getNumTiles(); j++) {
                    lightsFrustum.get(j).add(i);
                }
            }
        }

        /* Creates a texture containing the color and radius
         * information about all light sources. Position could
         * be added here, but for this example it is not due to
         * limitations in precision.
         */
        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i);
            color.set(l.color.r, l.color.g, l.color.b, 1.0f);
            lightInfoPixmap.setColor(color);
            lightInfoPixmap.drawPixel(0, i);
        }

        lightInfoTexture.draw(lightInfoPixmap, 0, 0);

        /* Creates a texture that contains a list of
         * light sources that are affecting each specific
         * tile. The row in the texture is decided by:
         * yTile*10+xTile and the following pixels on that
         * row are used to represent the ID of the light
         * sources.
         */
        for (int row = 0; row < lightGrid.getNumTiles(); row++) {
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

    private void setLights() {
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);
            lightTypes[k] = light.type.ordinal();

            lightAngles[i1++] = light.innerAngle;
            lightAngles[i1++] = light.outerAngle;

            vec1.set(light.position).mul(camera.view);

            lightPositionsAndRadiuses[i2++] = vec1.x;
            lightPositionsAndRadiuses[i2++] = vec1.y;
            lightPositionsAndRadiuses[i2++] = vec1.z;
            lightPositionsAndRadiuses[i2++] = light.radius;

            if (light.type != LightType.POINT_LIGHT) {
                vec1.set(light.direction).rot(camera.view);
            } else {
                vec1.set(0, 0, 0);
            }

            lightDirectionsAndIntensities[i3++] = vec1.x;
            lightDirectionsAndIntensities[i3++] = vec1.y;
            lightDirectionsAndIntensities[i3++] = vec1.z;
        }
    }

    private void setLightTypes() {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);
            lightTypes[k] = light.type.ordinal();
        }
    }

    private void setLightAngles() {
        int i = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);
            lightAngles[i++] = light.innerAngle;
            lightAngles[i++] = light.outerAngle;
        }
    }

    private void setLightPositionsAndRadiusesAndDirections() {
        int i1 = 0;
        int i2 = 0;

        for (int k = 0; k < lights.size; k++) {
            NhgLight light = lights.get(k);
            vec1.set(light.position).mul(camera.view);

            lightPositionsAndRadiuses[i1++] = vec1.x;
            lightPositionsAndRadiuses[i1++] = vec1.y;
            lightPositionsAndRadiuses[i1++] = vec1.z;
            lightPositionsAndRadiuses[i1++] = light.radius;

            vec1.set(light.direction).rot(camera.view);

            lightDirectionsAndIntensities[i2++] = vec1.x;
            lightDirectionsAndIntensities[i2++] = vec1.y;
            lightDirectionsAndIntensities[i2++] = vec1.z;
            lightDirectionsAndIntensities[i2++] = light.intensity;
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

        String renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase();

        if (renderer.contains("MALI")) {
            prefix += "#define GPU_MALI\n";
        } else if (renderer.contains("ADRENO")) {
            prefix += "#define GPU_ADRENO\n";
        }

        prefix += "#define GRID_SIZE " + gridSize + "\n";

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