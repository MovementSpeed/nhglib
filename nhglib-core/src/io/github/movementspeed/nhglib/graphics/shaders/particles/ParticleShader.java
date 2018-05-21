package io.github.movementspeed.nhglib.graphics.shaders.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.enums.OpenGLVersion;

/**
 * This is a custom shader to render the particles. Usually is not required, because the {@link DefaultShader} will be used
 * instead. This shader will be used when dealing with billboards using GPU mode or point sprites.
 *
 * @author inferno
 */
public class ParticleShader extends BaseShader {
    protected final Config config;

    /**
     * Material attributes which are not required but always supported.
     */
    private static final long optionalAttributes = IntAttribute.CullFace | DepthTestAttribute.Type;
    private static final Vector3 TMP_VECTOR3 = new Vector3();

    public static boolean softParticles = false;
    public static ParticleMode particleMode = ParticleMode.SOURCE_ALPHA;

    protected static long implementedFlags = BlendingAttribute.Type | TextureAttribute.Diffuse;

    private static String defaultVertexShader = null;
    private static String defaultFragmentShader = null;

    public RenderingSystem renderingSystem;

    private long materialMask;
    private long vertexMask;

    /**
     * The renderable used to create this shader, invalid after the call to init
     */
    private Renderable renderable;
    private Material currentMaterial;
    private Vector2 tmp;

    public ParticleShader(final Renderable renderable) {
        this(renderable, new Config());
    }

    public ParticleShader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }

    public ParticleShader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : getDefaultVertexShader(),
                config.fragmentShader != null ? config.fragmentShader : getDefaultFragmentShader());
    }

    public ParticleShader(final Renderable renderable, final Config config, final String prefix, final String vertexShader,
                          final String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public ParticleShader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        this.config = config;
        this.program = shaderProgram;
        this.renderable = renderable;

        tmp = new Vector2();

        materialMask = renderable.material.getMask() | optionalAttributes;
        vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();

        if (!config.ignoreUnimplemented && (implementedFlags & materialMask) != materialMask)
            throw new GdxRuntimeException("Some attributes not implemented yet (" + materialMask + ")");

        // Global uniforms
        //register(Inputs.screenWidth, Setters.screenWidth);
        register(Inputs.cameraRight, Setters.cameraRight);
        register(Inputs.cameraInvDirection, Setters.cameraInvDirection);
        register(DefaultShader.Inputs.cameraUp, Setters.cameraUp);
        register(DefaultShader.Inputs.cameraPosition, Setters.cameraPosition);

        // Object uniforms
        register(DefaultShader.Inputs.diffuseTexture, DefaultShader.Setters.diffuseTexture);

        register("u_softness", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, 0.6f);
            }
        });

        register("u_screen", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, tmp.set(
                        Gdx.graphics.getBackBufferWidth(),
                        Gdx.graphics.getBackBufferHeight()));
            }
        });

        register("u_cameraRange", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, tmp.set(shader.camera.near, shader.camera.far));
            }
        });

        register("u_depthTexture", new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if (renderingSystem != null && renderingSystem.getDepthTexture() != null) {
                    shader.set(inputID, renderingSystem.getDepthTexture());
                }
            }
        });

        register("u_viewMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.view);
            }
        });

        register("u_projectionMatrix", new GlobalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.projection);
            }
        });
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        init(program, renderable);
        renderable = null;
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);

        //context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //context.setDepthTest(GL20.GL_LEQUAL);
        //context.setDepthMask(true);
    }

    @Override
    public void render(final Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        bindMaterial(renderable);
        super.render(renderable);
    }

    @Override
    public void end() {
        currentMaterial = null;
        super.end();

        context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public boolean canRender(final Renderable renderable) {
        return (materialMask == (renderable.material.getMask() | optionalAttributes))
                && (vertexMask == renderable.meshPart.mesh.getVertexAttributes().getMask());
    }

    @Override
    public void dispose() {
        program.dispose();
        super.dispose();
    }

    @Override
    public int compareTo(Shader other) {
        if (other == null) return -1;
        if (other == this) return 0;
        return 0; // FIXME compare shaders on their impact on performance
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ParticleShader) ? equals((ParticleShader) obj) : false;
    }

    public static String getDefaultVertexShader() {
        if (defaultVertexShader == null)
            defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.vertex.glsl").readString();
        return defaultVertexShader;
    }

    public static String getDefaultFragmentShader() {
        if (defaultFragmentShader == null)
            defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.fragment.glsl")
                    .readString();
        return defaultFragmentShader;
    }

    public static String createPrefix(final Renderable renderable, final Config config) {
        String prefix = "";

        if (Nhg.glVersion == OpenGLVersion.VERSION_3 && Gdx.graphics.isGL30Available()) {
            prefix = "#version 300 es\n";
        }

        if (config.type == ParticleType.Billboard) {
            prefix += "#define billboard\n";
            if (config.align == AlignMode.Screen)
                prefix += "#define screenFacing\n";
            else if (config.align == AlignMode.ViewPoint) prefix += "#define viewPointFacing\n";
        }

        if (config.softParticles) {
            prefix += "#define SOFT_PARTICLES\n";
        }

        return prefix;
    }

    public void setDefaultCullFace(int cullFace) {
        config.defaultCullFace = cullFace;
    }

    public void setDefaultDepthFunc(int depthFunc) {
        config.defaultDepthFunc = depthFunc;
    }

    public boolean equals(ParticleShader obj) {
        return (obj == this);
    }

    public int getDefaultCullFace() {
        return config.defaultCullFace == -1 ? GL20.GL_BACK : config.defaultCullFace;
    }

    public int getDefaultDepthFunc() {
        return config.defaultDepthFunc == -1 ? GL20.GL_LEQUAL : config.defaultDepthFunc;
    }

    protected void bindMaterial(final Renderable renderable) {
        if (currentMaterial == renderable.material) return;

        int cullFace = config.defaultCullFace == -1 ?
                GL20.GL_BACK :
                config.defaultCullFace;

        int depthFunc = config.defaultDepthFunc == -1 ?
                GL20.GL_LEQUAL :
                config.defaultDepthFunc;

        float depthRangeNear = 0f;
        float depthRangeFar = 1f;
        boolean depthMask = true;

        currentMaterial = renderable.material;

        for (final Attribute attr : currentMaterial) {
            final long t = attr.type;

            if (BlendingAttribute.is(t)) {
                context.setBlending(true, particleMode.sFactor, particleMode.dFactor);
            } else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                DepthTestAttribute dta = (DepthTestAttribute) attr;
                depthFunc = dta.depthFunc;
                depthRangeNear = dta.depthRangeNear;
                depthRangeFar = dta.depthRangeFar;
                depthMask = dta.depthMask;
            } else if (!config.ignoreUnimplemented) {
                throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
            }
        }

        context.setCullFace(cullFace);
        context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
        context.setDepthMask(depthMask);
    }

    public static class Inputs {
        public final static Uniform cameraRight = new Uniform("u_cameraRight");
        public final static Uniform cameraInvDirection = new Uniform("u_cameraInvDirection");
    }

    public static class Setters {
        public final static Setter cameraRight = new Setter() {
            @Override
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, TMP_VECTOR3.set(shader.camera.direction).crs(shader.camera.up).nor());
            }
        };

        public final static Setter cameraUp = new Setter() {
            @Override
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, TMP_VECTOR3.set(shader.camera.up).nor());
            }
        };

        public final static Setter cameraInvDirection = new Setter() {
            @Override
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID,
                        TMP_VECTOR3.set(-shader.camera.direction.x, -shader.camera.direction.y, -shader.camera.direction.z).nor());
            }
        };
        public final static Setter cameraPosition = new Setter() {
            @Override
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.position);
            }
        };
    }

    public static class Config {
        public boolean softParticles = ParticleShader.softParticles;
        public boolean ignoreUnimplemented = true;

        /**
         * Set to 0 to disable culling
         */
        public int defaultCullFace = -1;
        /**
         * Set to 0 to disable depth test
         */
        public int defaultDepthFunc = -1;

        /**
         * The uber vertex shader to use, null to use the default vertex shader.
         */
        public String vertexShader = null;
        /**
         * The uber fragment shader to use, null to use the default fragment shader.
         */
        public String fragmentShader = null;

        public AlignMode align = AlignMode.Screen;
        public ParticleType type = ParticleType.Point;

        public Config() {
        }

        public Config(AlignMode align, ParticleType type) {
            this.align = align;
            this.type = type;
        }

        public Config(AlignMode align) {
            this.align = align;
        }

        public Config(ParticleType type) {
            this.type = type;
        }

        public Config(final String vertexShader, final String fragmentShader) {
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
        }
    }

    public enum ParticleType {
        Billboard, Point
    }

    public enum AlignMode {
        Screen, ViewPoint
    }

    public enum ParticleMode {
        ADDITIVE(GL30.GL_ONE, GL30.GL_ONE),
        SOURCE_ALPHA(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        public int sFactor, dFactor;

        ParticleMode(int sFactor, int dFactor) {
            this.sFactor = sFactor;
            this.dFactor = dFactor;
        }
    }
}