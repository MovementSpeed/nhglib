package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class SceneJson implements JsonParseable<Scene> {
    private Scene output;
    private Nhg nhg;

    public SceneJson(Nhg nhg) {
        this.nhg = nhg;
    }

    @Override
    public void parse(JsonValue jsonValue) {
        String name = jsonValue.getString("name");
        JsonValue entitiesJson = jsonValue.get("entities");

        output = new Scene(nhg, "root");
        output.name = name;

        int rootEntity = output.sceneGraph.getRootEntity();

        if (entitiesJson != null) {
            for (JsonValue entity : entitiesJson) {
                EntityJson entityJson = new EntityJson(nhg);
                entityJson.setSceneGraph(output.sceneGraph);
                entityJson.setParentEntity(rootEntity);
                entityJson.parse(entity);
            }
        }

        NodeComponent nodeComponent = nhg.entities.getComponent(rootEntity, NodeComponent.class);
        nodeComponent.applyTransforms();
    }

    @Override
    public Scene get() {
        return output;
    }
}
