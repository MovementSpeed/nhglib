package io.github.voidzombie.nhglib.graphics.worlds.impl;

import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.scenes.SceneManager;
import io.github.voidzombie.nhglib.graphics.worlds.base.NHGWorld;

/**
 * Created by Fausto Napoli on 28/12/2016.
 * Default NHGWorld implementation. Used for games with limited space worlds.
 */
public class DefaultWorld implements NHGWorld {
    private SceneManager sceneManager;
    private ArrayMap<String, Scene> scenes;

    public DefaultWorld() {
        sceneManager = new SceneManager();
        scenes = new ArrayMap<>();
    }

    @Override
    public void addScene(Scene scene) {
        scenes.put(scene.name, scene);
    }

    @Override
    public void loadScene(String name) {
        Scene scene = getScene(name);
        sceneManager.loadScene(scene);
    }

    @Override
    public void unloadScene(String name) {
        Scene scene = getScene(name);
        sceneManager.unloadScene(scene);
    }

    @Override
    public void setBounds() {

    }

    @Override
    public void update() {

    }

    @Override
    public Scene getScene(String name) {
        return scenes.get(name);
    }
}
