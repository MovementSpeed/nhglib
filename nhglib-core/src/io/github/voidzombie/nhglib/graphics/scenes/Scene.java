package io.github.voidzombie.nhglib.graphics.scenes;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class Scene {
    public String name;
    public SceneGraph sceneGraph;

    public Scene() {
        sceneGraph = new SceneGraph();
    }
}
