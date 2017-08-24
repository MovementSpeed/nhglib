package io.github.movementspeed.nhglib.graphics.worlds;

import com.badlogic.gdx.utils.ArrayMap;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.scenes.SceneManager;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;
import io.github.movementspeed.nhglib.runtime.messaging.Messaging;
import io.github.movementspeed.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 28/12/2016.
 * Manages how the engine should handle the game world\space.
 */
public class NhgWorld {
    private Entities entities;
    private Bounds bounds;
    private SceneManager sceneManager;
    private WorldStrategy worldStrategy;
    private NodeComponent referenceNodeComponent;

    private ArrayMap<String, Scene> scenes;

    public NhgWorld(Messaging messaging, Entities entities, Assets assets, WorldStrategy strategy) {
        this(messaging, entities, assets, strategy, new Bounds(1f, 1f, 1f));
    }

    public NhgWorld(Messaging messaging, Entities entities, Assets assets, WorldStrategy strategy, Bounds bounds) {
        this.entities = entities;
        this.worldStrategy = strategy;
        this.bounds = bounds;

        sceneManager = new SceneManager(messaging, entities, assets);
        scenes = new ArrayMap<>();
    }

    public void addScene(Scene scene) {
        scenes.put(scene.name, scene);
    }

    public void loadScene(Scene scene) {
        addScene(scene);
        loadScene(scene.name);
    }

    public void loadScene(String name) {
        Scene scene = getScene(name);
        sceneManager.loadScene(scene);
    }

    public void unloadScene(String name) {
        Scene scene = getScene(name);
        sceneManager.unloadScene(scene);
    }

    public void update() {
        worldStrategy.update(sceneManager.getCurrentScene(), bounds, referenceNodeComponent);
    }

    public void setReferenceEntity(String entityName) {
        if (entityName != null && !entityName.isEmpty()) {
            int referenceEntity =
                    sceneManager.getCurrentScene().sceneGraph.getSceneEntity(entityName);

            referenceNodeComponent =
                    entities.getComponent(referenceEntity, NodeComponent.class);
        }
    }

    public Scene getScene(String name) {
        return scenes.get(name);
    }

    public Scene getCurrentScene() {
        return sceneManager.getCurrentScene();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
