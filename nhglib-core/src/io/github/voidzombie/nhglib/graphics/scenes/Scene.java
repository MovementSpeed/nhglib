package io.github.voidzombie.nhglib.graphics.scenes;

import io.github.voidzombie.nhglib.Nhg;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class Scene {
    public String name;
    public SceneGraph sceneGraph;

    public Scene(Nhg nhg, String rootId) {
        sceneGraph = new SceneGraph(nhg, rootId);
    }
}
