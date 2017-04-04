package io.github.voidzombie.nhglib.graphics.scenes;

import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class Scene {
    public String name;
    public SceneGraph sceneGraph;

    public Scene(Entities entities, String rootId) {
        sceneGraph = new SceneGraph(entities, rootId);
    }
}
