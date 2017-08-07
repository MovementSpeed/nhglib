package io.github.voidzombie.nhglib.graphics.shaders.depth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

public class DepthMapShader extends BaseShader {
    private float bones[];
    private Params params;
    private Matrix4 idtMatrix;
    private Renderable renderable;

    @Override
    public void end() {
        super.end();
    }

    public DepthMapShader(final Renderable renderable, Params params) {
        this.renderable = renderable;
        this.params = params;

        String prefix = createPrefix(renderable);

        String vert = prefix + Gdx.files.internal("shaders/depth_shader.vert").readString();
        String frag = prefix + Gdx.files.internal("shaders/depth_shader.frag").readString();

        ShaderProgram.pedantic = false;
        program = new ShaderProgram(vert, frag);

        String shaderLog = program.getLog();

        if (!program.isCompiled()) {
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

        register("u_cameraFar", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, camera.far);
            }
        });

        register("u_lightPosition", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, camera.position);
            }
        });
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(final Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        updateBones(renderable);
        super.render(renderable);
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;

        idtMatrix = new Matrix4();
        bones = new float[0];
    }

    @Override
    public int compareTo(final Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(final Renderable instance) {
        boolean bones = ShaderUtils.useBones(instance) == params.useBones;
        return bones;
    }

    private void updateBones(Renderable renderable) {
        if (renderable.bones != null) {
            bones = new float[renderable.bones.length * 16];

            for (int i = 0; i < bones.length; i++) {
                final int idx = i / 16;
                bones[i] = (idx >= renderable.bones.length || renderable.bones[idx] == null) ?
                        idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16];
            }

            program.setUniformMatrix4fv("u_bones", bones, 0, bones.length);
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

        return prefix;
    }

    public static class Params {
        boolean useBones;
    }
}