package io.github.movementspeed.nhgpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.InputSystem;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.PhysicsSystem;
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.core.messaging.Message;
import io.github.movementspeed.nhglib.files.HDRData;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.GltfModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.GltfModelReader;
import io.github.movementspeed.nhglib.graphics.lights.LightProbe;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.AmbientLightingAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute;
import io.github.movementspeed.nhglib.graphics.worlds.NhgWorld;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.movementspeed.nhglib.input.enums.InputAction;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.utils.data.Bounds;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;
import io.reactivex.functions.Consumer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Playground extends NhgEntry implements InputListener {
	private Scene scene;
	private NhgWorld world;
	private RenderingSystem renderingSystem;
	private Environment environment;
	private NodeComponent cameraNode;
	private CameraComponent cameraComponent;
	private LightProbe lightProbe;

	@Override
	public void onStart() {
		super.onStart();
		Nhg.debugLogs = true;
		Nhg.debugFpsLogs = true;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		readGLTF();

		world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
				new DefaultWorldStrategy(),
				new Bounds(2f, 2f, 2f));

		nhg.assets.queueAsset(new Asset("scene", "scenes/scene.json", Scene.class));

		InputSystem inputSystem = nhg.entities.getEntitySystem(InputSystem.class);
		inputSystem.loadMapping("input/input.json");
		inputSystem.setEnableContext("game", true);
		inputSystem.setEnableContext("menu", true);
		inputSystem.addInputListener(this);

		PhysicsSystem physicsSystem = nhg.entities.getEntitySystem(PhysicsSystem.class);
		physicsSystem.setGravity(new Vector3(0, -0.5f, 0));

		renderingSystem = nhg.entities.getEntitySystem(RenderingSystem.class);
		renderingSystem.setClearColor(Color.DARK_GRAY);

		environment = renderingSystem.getEnvironment();

		NhgLightsAttribute lightsAttribute = new NhgLightsAttribute();

		float pos = 5f;

        for (int i = 0; i < 40; i++) {
            NhgLight light = NhgLight.point(15, 2.8f,
                    new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 1f));
            light.position.set(new Vector3(MathUtils.random(-pos, pos), MathUtils.random(-pos, pos), MathUtils.random(-pos, pos)));
            lightsAttribute.lights.add(light);
        }

        NhgLight sun = NhgLight.directional(5, Color.WHITE);
        sun.direction.set(1, -1, -1);
        lightsAttribute.lights.add(sun);

		GammaCorrectionAttribute gammaCorrectionAttribute = new GammaCorrectionAttribute(true);
		AmbientLightingAttribute ambientLightingAttribute = new AmbientLightingAttribute(0.03f);

		environment.set(lightsAttribute);
		environment.set(gammaCorrectionAttribute);
		environment.set(ambientLightingAttribute);

		// Subscribe to asset events
		nhg.messaging.get(Strings.Events.assetLoaded, Strings.Events.assetLoadingFinished, Strings.Events.sceneLoaded)
				.subscribe(new Consumer<Message>() {
					@Override
					public void accept(Message message) {
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
							}
						} else if (message.is(Strings.Events.sceneLoaded)) {
							NhgLogger.log(this, "Scene loaded");
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
	}

	@Override
	public void onInput(NhgInput input) {
		InputAction action = input.getAction();

		if (action != null && action == InputAction.DOWN) {
			NhgLogger.log(this, "You pressed %s", input.getName());

			if (input.is("exit")) {
				nhg.messaging.send(new Message(Strings.Events.engineDestroy));
			}
		}
	}

	private void readGLTF() {
		FileHandle file = Gdx.files.internal("/models/box.gltf");
		GltfModelReader gltfModelReader = new GltfModelReader();
		GltfModel gltfModel = null;

		String path = file.file().getAbsolutePath();

		try {
			URI uri = new URI(path);
			gltfModel = gltfModelReader.read(uri);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (gltfModel != null) {
			int c = 0;
			c++;
		}
	}
}
