package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class SceneJson implements JsonParseable<Scene> {
    public String name;
    public Array<EntityJson> entities;

    private Scene output;

    public SceneJson() {
        name = "default";
        entities = new Array<>();

        output = new Scene();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        this.name = jsonValue.getString("name");
        JsonValue entitiesJson = jsonValue.get("entities");

        int rootEntity = output.sceneGraph.getRootEntity();
        //NodeComponent nodeComponent = NHG.entitySystem.getComponent(rootEntity, NodeComponent.class);

        for (JsonValue entity : entitiesJson) {
            EntityJson entityJson = new EntityJson();
            entityJson.sceneGraphRef = output.sceneGraph;
            entityJson.parentEntity = rootEntity;
            entityJson.parse(entity);

            entities.add(entityJson);
        }
    }

    @Override
    public Scene get() {
        return output;
    }
}
