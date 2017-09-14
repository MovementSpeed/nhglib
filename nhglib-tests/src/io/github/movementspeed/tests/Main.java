package io.github.movementspeed.tests;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.files.HDRData;
import io.github.movementspeed.nhglib.graphics.lights.LightProbe;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.particles.ParticleShader;
import io.github.movementspeed.nhglib.graphics.worlds.NhgWorld;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.NhgInput;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.CameraSystem;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.runtime.entry.NhgEntry;
import io.github.movementspeed.nhglib.runtime.messaging.Message;
import io.github.movementspeed.nhglib.utils.data.Bounds;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.github.movementspeed.tests.systems.TestNodeSystem;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class Main extends NhgEntry implements InputListener {
    private Scene scene;
    private NhgWorld world;
    private NodeComponent cameraNode;
    private CameraComponent cameraComponent;
    private RenderingSystem renderingSystem;
    private Environment environment;

    private ShaderProgram simpleCubemapShader;
    private LightProbe lightProbe;
    private Mesh cubeMesh;
    FitViewport fitViewport;

    @Override
    public void onStart() {
        super.onStart();
        Nhg.debugLogs = true;
        Nhg.debugDrawPhysics = false;
        ParticleShader.softParticles = false;

        simpleCubemapShader = new ShaderProgram(
                Gdx.files.internal("shaders/simple_cubemap.vert"),
                Gdx.files.internal("shaders/simple_cubemap.frag"));

        ModelBuilder mb = new ModelBuilder();
        Model cube = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        cubeMesh = cube.meshes.first();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();

        world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
                new DefaultWorldStrategy(),
                new Bounds(2f, 2f, 2f));

        nhg.input.addListener(this);

        nhg.assets.queueAsset(new Asset("scene", "myscene.nhs", Scene.class));
        nhg.assets.queueAsset(new Asset("inputMap", "input.nhc", JsonValue.class));

        renderingSystem = nhg.entities.getEntitySystem(RenderingSystem.class);
        renderingSystem.setClearColor(Color.GRAY);
        renderingSystem.setRenderScale(0.25f);

        environment = renderingSystem.getEnvironment();

        NhgLightsAttribute lightsAttribute = new NhgLightsAttribute();

        for (int i = 0; i < 10; i++) {
            NhgLight light = NhgLight.point(10, 10, new Color(Color.rgba8888(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 1f)));
            light.position.set(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f));
            lightsAttribute.lights.add(light);
        }

        GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute();
        gammaCorrectionAttribute.gammaCorrection = true;

        environment.set(lightsAttribute);
        environment.set(gammaCorrectionAttribute);

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

                            CameraSystem cameraSystem = nhg.entities.getEntitySystem(CameraSystem.class);
                            PerspectiveCamera perspectiveCamera = (PerspectiveCamera) cameraSystem.cameras.first();
                            perspectiveCamera.viewportWidth = 960;
                            perspectiveCamera.viewportHeight = 540;

                            /*fitViewport = new FitViewport(640, 360, perspectiveCamera);
                            fitViewport.update(1280, 720);*/

                            HDRData data = nhg.assets.get("newport_loft");

                            lightProbe = new LightProbe();
                            lightProbe.build(data,
                                    128f, 128f,
                                    32f, 32f,
                                    64f, 64f,
                                    128f, 128f);

                            IBLAttribute irradianceAttribute = IBLAttribute.createIrradiance(lightProbe.getIrradiance());
                            IBLAttribute prefilterAttribute = IBLAttribute.createPrefilter(lightProbe.getPrefilter());
                            IBLAttribute brdfAttribute = IBLAttribute.createBrdf(lightProbe.getBrdf());

                            environment.set(irradianceAttribute);
                            environment.set(prefilterAttribute);
                            environment.set(brdfAttribute);
                        }
                    }
                });
    }

    @Override
    public void onUpdate(float delta) {
        super.onUpdate(delta);
        world.update();

        /*if (cameraComponent != null) {
            lightProbe.getEnvironment().bind(0);
            simpleCubemapShader.begin();
            simpleCubemapShader.setUniformMatrix("u_view", cameraComponent.camera.view);
            simpleCubemapShader.setUniformMatrix("u_projection", cameraComponent.camera.projection);
            simpleCubemapShader.setUniformi("u_environmentMap", 0);
            cubeMesh.render(simpleCubemapShader, GL20.GL_TRIANGLES);
            simpleCubemapShader.end();
        }*/
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
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
}