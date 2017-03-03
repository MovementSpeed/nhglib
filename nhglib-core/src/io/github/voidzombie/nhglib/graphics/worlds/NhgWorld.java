package io.github.voidzombie.nhglib.graphics.worlds;

import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.scenes.SceneManager;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 28/12/2016.
 * Manages how the engine should handle the game world\space.
 */
public class NhgWorld {
    private Bounds bounds;
    private SceneManager sceneManager;
    private WorldStrategy worldStrategy;
    private NodeComponent referenceNodeComponent;

    private ArrayMap<String, Scene> scenes;

    public NhgWorld(WorldStrategy strategy) {
        this(strategy, new Bounds(1f, 1f, 1f));
    }

    public NhgWorld(WorldStrategy strategy, Bounds bounds) {
        this.worldStrategy = strategy;
        this.bounds = bounds;

        sceneManager = new SceneManager();
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
            Integer referenceEntity =
                    sceneManager.getCurrentScene().sceneGraph.getSceneEntity(entityName);

            referenceNodeComponent =
                    Nhg.entitySystem.getComponent(referenceEntity, NodeComponent.class);
        }
    }

    public Scene getScene(String name) {
        return scenes.get(name);
    }
}
