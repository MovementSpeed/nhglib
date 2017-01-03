package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class SceneJson implements JsonParseable<Scene> {
    private Scene output;

    @Override
    public void parse(JsonValue jsonValue) {
        String name = jsonValue.getString("name");
        JsonValue entitiesJson = jsonValue.get("entities");

        output = new Scene("root");
        output.name = name;

        int rootEntity = output.sceneGraph.getRootEntity();

        for (JsonValue entity : entitiesJson) {
            EntityJson entityJson = new EntityJson();
            entityJson.setSceneGraph(output.sceneGraph);
            entityJson.setParentEntity(rootEntity);
            entityJson.parse(entity);
        }

        NodeComponent nodeComponent = NHG.entitySystem.getComponent(rootEntity, NodeComponent.class);
        nodeComponent.applyTransforms();
    }

    @Override
    public Scene get() {
        return output;
    }
}
