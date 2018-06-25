package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class SimpleShader extends BaseShader {
    private int maxBonesLength = Integer.MIN_VALUE;
    private int bonesIID;
    private int bonesLoc;
    private float bones[];

    private Vector3 vec1 = new Vector3();
    private Matrix4 idtMatrix;

    private Camera camera;
    private Params params;
    private Renderable renderable;
    private ShaderProgram shaderProgram;

    public SimpleShader(Renderable renderable, Params params) {
        this.renderable = renderable;
        this.params = params;

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
        String frag = prefix + Gdx.files.internal(folder + "simple_shader.frag").readString();

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
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return ShaderUtils.useBones(instance) == params.useBones;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        Gdx.gl.glColorMask(false, false, false, false);
        super.begin(camera, context);
    }

    @Override
    public void render(Renderable renderable) {
        super.render(renderable);
    }

    @Override
    public void end() {
        super.end();
        Gdx.gl.glColorMask(true, true, true, true);
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();
        super.dispose();
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
    }
}