package io.github.voidzombie.tests;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.lights.NhgLight;
import io.github.voidzombie.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.voidzombie.nhglib.graphics.ogl.NhgFrameBufferCubemap;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.voidzombie.nhglib.graphics.shaders.particles.ParticleShader;
import io.github.voidzombie.nhglib.graphics.worlds.NhgWorld;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.voidzombie.nhglib.input.interfaces.InputListener;
import io.github.voidzombie.nhglib.input.models.NhgInput;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.RenderingSystem;
import io.github.voidzombie.nhglib.runtime.entry.NhgEntry;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bounds;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.debug.NhgLogger;
import io.github.voidzombie.tests.systems.TestNodeSystem;
import io.reactivex.functions.Consumer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NhgEntry implements InputListener {
    private Scene scene;
    private NhgWorld world;
    private NodeComponent cameraNode;
    private CameraComponent cameraComponent;
    private RenderingSystem renderingSystem;

    private Mesh quadMesh;
    private ModelInstance cubeModelInstance;
    private ShaderProgram simpleCubemapShader;
    private Cubemap environmentCubemap;
    private Cubemap irradianceCubemap;
    private Cubemap prefilteredCubemap;
    private Texture brdfTexture;

    @Override
    public void engineStarted() {
        super.engineStarted();
        Nhg.debugLogs = true;
        Nhg.debugDrawPhysics = false;
        ParticleShader.softParticles = false;

        simpleCubemapShader = new ShaderProgram(
                Gdx.files.internal("shaders/simple_cubemap.vert"),
                Gdx.files.internal("shaders/simple_cubemap.frag"));

        ModelBuilder mb = new ModelBuilder();
        Model cubeModel = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);

        cubeModelInstance = new ModelInstance(cubeModel);

        environmentCubemap = equirectangularHdrToCubemap("textures/test_hdr.hdr", 1024, 1024);
        irradianceCubemap = renderIrradiance(environmentCubemap);
        prefilteredCubemap = renderPrefilter(environmentCubemap);
        brdfTexture = renderBRDF();
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();

        world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
                new DefaultWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        nhg.input.addListener(this);

        nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        renderingSystem = nhg.entities.getEntitySystem(RenderingSystem.class);
        renderingSystem.setClearColor(Color.GRAY);
        //renderingSystem.addRenderingInterfaces(this);

        Environment environment = renderingSystem.getEnvironment();

        NhgLightsAttribute lightsAttribute = new NhgLightsAttribute();
        lightsAttribute.lights.add(NhgLight.point(5, 10, Color.WHITE));

        GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute();
        gammaCorrectionAttribute.gammaCorrection = true;

        IBLAttribute iblAttribute = IBLAttribute.createIrradiance(irradianceCubemap);

        environment.set(lightsAttribute);
        environment.set(gammaCorrectionAttribute);
        environment.set(iblAttribute);

        // Subscribe to asset events
        nhg.messaging.get(Strings.Events.assetLoaded, Strings.Events.assetLoadingFinished, Strings.Events.sceneLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Strings.Events.assetLoaded)) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (asset.is("scene")) {
                                scene = nhg.assets.get(asset);

                                world.loadScene(scene);
                                world.setReferenceEntity("camera");

                                Integer cameraEntity = scene.sceneGraph.getSceneEntity("camera");
                                cameraNode = nhg.entities.getComponent(
                                        cameraEntity, NodeComponent.class);

                                cameraComponent = nhg.entities.getComponent(cameraEntity, CameraComponent.class);
                            } else if (asset.is("inputMap")) {
                                nhg.input.fromJson((JsonValue) nhg.assets.get(asset));
                                nhg.input.setActiveContext("game", true);
                                nhg.input.setActiveContext("global", true);
                            }
                        } else if (message.is(Strings.Events.sceneLoaded)) {
                            NhgLogger.log(this, "Scene loaded");
                        }
                    }
                });
    }

    @Override
    public void engineUpdate(float delta) {
        super.engineUpdate(delta);
        world.update();

        if (cameraComponent != null) {
            brdfTexture.bind(0);
            simpleCubemapShader.begin();
            simpleCubemapShader.setUniformMatrix("u_view", cameraComponent.camera.view);
            simpleCubemapShader.setUniformMatrix("u_projection", cameraComponent.camera.projection);
            simpleCubemapShader.setUniformi("u_environmentMap", 0);
            cubeModelInstance.model.meshes.first().render(simpleCubemapShader, GL20.GL_TRIANGLES);
            //quadMesh.render(simpleCubemapShader, GL20.GL_TRIANGLES);
            simpleCubemapShader.end();
        }
    }

    @Override
    public Array<BaseSystem> onConfigureEntitySystems() {
        Array<BaseSystem> systems = new Array<>();
        systems.add(new TestNodeSystem());

        return systems;
    }

    @Override
    public void onKeyInput(NhgInput input) {
        if (scene != null) {
            NodeComponent nodeComponent = cameraNode;

            switch (input.getName()) {
                case "strafeRight":
                    nodeComponent.translate(0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "strafeLeft":
                    nodeComponent.translate(-0.5f * Gdx.graphics.getDeltaTime(), 0, 0);
                    break;

                case "forward":
                    nodeComponent.translate(0, 0, -0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "backward":
                    nodeComponent.translate(0, 0, 0.5f * Gdx.graphics.getDeltaTime());
                    break;

                case "jump":
                    cameraNode.rotate(0, 0, 0.1f);
                    break;

                case "sprint":
                    cameraNode.rotate(0, 0, -0.1f);
                    break;

                case "exit":
                    nhg.messaging.send(new Message(Strings.Events.engineDestroy));
                    break;
            }

            nodeComponent.applyTransforms();
        }
    }

    @Override
    public void onStickInput(NhgInput input) {
        if (scene != null) {
            NodeComponent nodeComponent = cameraNode;

            Vector2 stickVector = (Vector2) input.getInputSource().getValue();

            if (stickVector != null) {
                switch (input.getName()) {
                    case "movementStick":
                        nodeComponent.translate(
                                stickVector.x * Gdx.graphics.getDeltaTime(),
                                0,
                                stickVector.y * Gdx.graphics.getDeltaTime(),
                                true);
                        break;

                    case "rotationStick":
                        nodeComponent.rotate(
                                stickVector.y * Gdx.graphics.getDeltaTime(),
                                stickVector.x * Gdx.graphics.getDeltaTime(),
                                0,
                                true
                        );
                        break;
                }
            }
        }
    }

    @Override
    public void onPointerInput(NhgInput input) {
        Vector2 pointerVector = (Vector2) input.getInputSource().getValue();

        if (pointerVector != null) {
            switch (input.getName()) {
                case "look":
                    float horizontalAxis = pointerVector.x;
                    float verticalAxis = pointerVector.y;

                    cameraNode.rotate(verticalAxis, horizontalAxis, 0);
                    cameraNode.applyTransforms();
                    break;
            }
        }
    }

    @Override
    public void onMouseInput(NhgInput input) {
        Vector2 pointerVector = (Vector2) input.getInputSource().getValue();

        if (pointerVector != null) {
            float horizontalAxis = pointerVector.x;
            float verticalAxis = pointerVector.y;

            cameraNode.rotate(verticalAxis, horizontalAxis, 0);
            cameraNode.applyTransforms();
        }
    }

    private Cubemap equirectangularHdrToCubemap(String texturePath, int width, int height) {
        Texture equirectangularTexture = null;
        ShaderProgram equiToCubeShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/equi_to_cube_shader.frag"));

        try {
            FileHandle fh = Gdx.files.internal(texturePath);
            BufferedImage bufferedImage = ImageIO.read(fh.file());

            int bWidth = bufferedImage.getWidth();
            int bHeight = bufferedImage.getHeight();

            Pixmap pixmap = new Pixmap(bWidth, bHeight, Pixmap.Format.RGB888);

            for (int x = 0; x < bWidth; x++) {
                for (int y = 0; y < bHeight; y++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    pixmap.drawPixel(x, bHeight - y, rgb);
                }
            }

            equirectangularTexture = new Texture(pixmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Array<PerspectiveCamera> perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, 512, 512);
            pc.near = 0.1f;
            pc.far = 10.0f;
            perspectiveCameras.add(pc);
        }

        PerspectiveCamera pc1 = perspectiveCameras.get(0);
        pc1.lookAt(1, 0, 0);
        pc1.rotate(Vector3.X, 180);
        pc1.update();

        PerspectiveCamera pc2 = perspectiveCameras.get(1);
        pc2.lookAt(0, 0, -1);
        pc2.rotate(Vector3.X, 180);
        pc2.update();

        PerspectiveCamera pc3 = perspectiveCameras.get(2);
        pc3.lookAt(0, 0, 1);
        pc3.rotate(Vector3.X, 180);
        pc3.update();

        PerspectiveCamera pc4 = perspectiveCameras.get(3);
        pc4.lookAt(0, 1, 0);
        pc4.rotate(Vector3.Y, 270);
        pc4.update();

        PerspectiveCamera pc5 = perspectiveCameras.get(4);
        pc5.lookAt(0, -1, 0);
        pc5.rotate(Vector3.Y, 270);
        pc5.update();

        PerspectiveCamera pc6 = perspectiveCameras.get(5);
        pc6.lookAt(-1, 0, 0);
        pc6.rotate(Vector3.X, 180);
        pc6.update();

        FrameBufferCubemap frameBufferCubemap = new FrameBufferCubemap(Pixmap.Format.RGB888,
                width, height, true);

        equirectangularTexture.bind(0);
        equiToCubeShader.begin();
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        equiToCubeShader.setUniformi("u_equirectangularMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeModelInstance.model.meshes.first().render(equiToCubeShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide();
        }
        frameBufferCubemap.end();
        equiToCubeShader.end();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderIrradiance(Cubemap environmentCubemap) {
        ShaderProgram irradianceShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/irradiance_shader.frag"));

        Array<PerspectiveCamera> perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, 32, 32);
            pc.near = 0.1f;
            pc.far = 10.0f;
            perspectiveCameras.add(pc);
        }

        PerspectiveCamera pc1 = perspectiveCameras.get(0);
        pc1.lookAt(1, 0, 0);
        pc1.rotate(Vector3.X, 180);
        pc1.update();

        PerspectiveCamera pc2 = perspectiveCameras.get(1);
        pc2.lookAt(0, 0, -1);
        pc2.rotate(Vector3.X, 180);
        pc2.update();

        PerspectiveCamera pc3 = perspectiveCameras.get(2);
        pc3.lookAt(0, 0, 1);
        pc3.rotate(Vector3.X, 180);
        pc3.update();

        PerspectiveCamera pc4 = perspectiveCameras.get(3);
        pc4.lookAt(0, 1, 0);
        pc4.rotate(Vector3.Y, 270);
        pc4.update();

        PerspectiveCamera pc5 = perspectiveCameras.get(4);
        pc5.lookAt(0, -1, 0);
        pc5.rotate(Vector3.Y, 270);
        pc5.update();

        PerspectiveCamera pc6 = perspectiveCameras.get(5);
        pc6.lookAt(-1, 0, 0);
        pc6.rotate(Vector3.X, 180);
        pc6.update();

        FrameBufferCubemap frameBufferCubemap = new FrameBufferCubemap(Pixmap.Format.RGB888,
                32, 32, true);

        environmentCubemap.bind(0);
        irradianceShader.begin();
        irradianceShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        irradianceShader.setUniformi("u_environmentMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            irradianceShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeModelInstance.model.meshes.first().render(irradianceShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide();
        }
        frameBufferCubemap.end();
        irradianceShader.end();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderPrefilter(Cubemap environmentCubemap) {
        ShaderProgram prefilterShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/prefilter_shader.frag"));

        Array<PerspectiveCamera> perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, 128, 128);
            pc.near = 0.1f;
            pc.far = 10.0f;
            perspectiveCameras.add(pc);
        }

        PerspectiveCamera pc1 = perspectiveCameras.get(0);
        pc1.lookAt(0, 0, 1);
        pc1.rotate(Vector3.Z, 180);
        pc1.update();

        PerspectiveCamera pc2 = perspectiveCameras.get(1);
        pc2.lookAt(0, 0, -1);
        pc2.rotate(Vector3.Z, 180);
        pc2.update();

        // top
        PerspectiveCamera pc3 = perspectiveCameras.get(2);
        pc3.rotate(Vector3.Z, 90);
        pc3.lookAt(0, 1, 0);
        pc3.update();

        // down
        PerspectiveCamera pc4 = perspectiveCameras.get(3);
        pc4.rotate(Vector3.Z, 270);
        pc4.lookAt(0, -1, 0);
        pc4.update();

        // forward
        PerspectiveCamera pc5 = perspectiveCameras.get(4);
        pc5.lookAt(-1, 0, 0);
        pc5.rotate(Vector3.X, 180);
        pc5.update();

        // back
        PerspectiveCamera pc6 = perspectiveCameras.get(5);
        pc6.lookAt(1, 0, 0);
        pc6.rotate(Vector3.X, 180);
        pc6.update();

        NhgFrameBufferCubemap frameBufferCubemap = new NhgFrameBufferCubemap(Pixmap.Format.RGB888,
                128, 128, true);

        prefilterShader.begin();
        prefilterShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        prefilterShader.setUniformi("u_environment", 0);
        frameBufferCubemap.begin();
        environmentCubemap.bind(0);
        int maxMipLevels = 5;
        for (int mip = 0; mip < maxMipLevels; mip++) {
            // reisze framebuffer according to mip-level size.
            double ml = Math.pow(0.5, (double) mip);

            int mipWidth = (int) (128f * ml);
            int mipHeight = (int) (128f * ml);

            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, frameBufferCubemap.getDepthBufferHandle());
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, mipWidth, mipHeight);
            Gdx.gl.glViewport(0, 0, mipWidth, mipHeight);

            float roughness = (float) mip / (float) (maxMipLevels - 1);
            prefilterShader.setUniformf("u_roughness", roughness);

            for (int i = 0; i < 6; ++i) {
                prefilterShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);

                frameBufferCubemap.bindSide(i, mip);

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                cubeModelInstance.model.meshes.first().render(prefilterShader, GL20.GL_TRIANGLES);
            }
        }
        frameBufferCubemap.end();
        prefilterShader.end();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Texture renderBRDF() {
        G3dModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        Model quad = modelLoader.loadModel(Gdx.files.internal("models/quad.g3db"));
        quadMesh = quad.meshes.first();

        ShaderProgram brdfShader = new ShaderProgram(
                Gdx.files.internal("shaders/brdf_shader.vert"),
                Gdx.files.internal("shaders/brdf_shader.frag"));

        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 512, 512, true);

        brdfShader.begin();
        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, 512, 512);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        quadMesh.render(brdfShader, GL20.GL_TRIANGLES);
        frameBuffer.end();
        brdfShader.end();

        return frameBuffer.getColorBufferTexture();
    }
}