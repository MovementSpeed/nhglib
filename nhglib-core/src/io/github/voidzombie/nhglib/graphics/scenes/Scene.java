package io.github.voidzombie.nhglib.graphics.scenes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.SceneJson;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class Scene implements JsonParseable<Scene> {
    public String name;
    public SceneGraph sceneGraph;

    public Scene() {
        sceneGraph = new SceneGraph();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        JsonValue sceneRoot = jsonValue.get("scene");
        SceneJson sceneJson = new SceneJson();
        sceneJson.parse(sceneRoot);
    }

    @Override
    public Scene get() {
        return this;
    }
}
