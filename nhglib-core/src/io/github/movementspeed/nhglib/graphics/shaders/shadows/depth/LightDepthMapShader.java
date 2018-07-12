package io.github.movementspeed.nhglib.graphics.shaders.shadows.depth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

public class LightDepthMapShader extends BaseShader {
    private int maxBonesLength = Integer.MIN_VALUE;

    private int bonesIID;
    private int bonesLoc;

    private Params params;
    private Matrix4 idtMatrix;
    private Renderable renderable;

    private float bones[];
    private Array<NhgLight> shadowLights;

    public LightDepthMapShader(final Renderable renderable, Array<NhgLight> shadowLights, Params params) {
        this.renderable = renderable;
        this.shadowLights = shadowLights;
        this.params = params;

        String prefix = createPrefix(renderable);

        String vert = prefix + Gdx.files.internal("shaders/light_depth_map_shader.vert").readString();
        String frag = prefix + Gdx.files.internal("shaders/light_depth_map_shader.frag").readString();

        ShaderProgram.pedantic = false;
        program = new ShaderProgram(vert, frag);

        String shaderLog = program.getLog();

        if (!program.isCompiled()) {
            throw new GdxRuntimeException(shaderLog);
        }

        register("u_mvpMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, camera.combined);
            }
        });

        register("u_modelMatrix", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, renderable.worldTransform);
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

                    program.setUniformMatrix4fv(bonesLoc, bones, 0, renderableBonesLength);
                }
            }
        });
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;

        idtMatrix = new Matrix4();
        bones = new float[0];
        bonesLoc = loc(bonesIID);
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        NhgLight light = shadowLights.get(0);
        program.setUniformf("u_lightPosition", light.position);
        program.setUniformf("u_cameraFar", light.shadowLightProperties.lightCamera.far);
        super.render(renderable, combinedAttributes);
    }

    @Override
    public int compareTo(final Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance) {
        return ShaderUtils.useBones(instance) == params.useBones;
    }

    private String createPrefix(Renderable renderable) {
        String prefix = "";

        if (Gdx.graphics.isGL30Available()) {
            switch (Nhg.glVersion) {
                case VERSION_2:
                    prefix = "#define GLVERSION 2\n";
                    break;

                case VERSION_3:
                    prefix = "#version 300 es\n";
                    prefix += "#define GLVERSION 3\n";
                    break;
            }
        } else {
            prefix = "#define GLVERSION 2\n";
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

        return prefix;
    }

    public static class Params {
        boolean useBones;
    }
}
