package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;
import io.github.movementspeed.nhglib.utils.scenes.SceneUtils;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class EntityJson implements JsonParseable<Integer> {
    public int parentEntity;

    private int output;

    private Nhg nhg;
    private SceneGraph sceneGraph;

    public EntityJson(Nhg nhg) {
        this.nhg = nhg;
    }

    @Override
    public void parse(JsonValue jsonValue) {
        String id = jsonValue.getString("id");
        boolean attachToParent = jsonValue.getBoolean("attachToParent", true);

        int entity;

        if (attachToParent) {
            entity = sceneGraph.addSceneEntity(id, parentEntity);
        } else {
            entity = sceneGraph.addSceneEntity(id);
        }

        JsonValue componentsJson = jsonValue.get("components");

        for (JsonValue componentJsonValue : componentsJson) {
            String type = componentJsonValue.getString("type");
            ComponentJson componentJson = SceneUtils.componentJsonFromType(type);

            if (componentJson != null) {
                componentJson.parentEntity = parentEntity;
                componentJson.entity = entity;
                componentJson.nhg = nhg;
                componentJson.sceneGraph = sceneGraph;
                componentJson.parse(componentJsonValue);
            }
        }

        JsonValue entitiesJson = jsonValue.get("entities");

        if (entitiesJson != null) {
            for (JsonValue entityJsonValue : entitiesJson) {
                EntityJson entityJson = new EntityJson(nhg);
                entityJson.sceneGraph = sceneGraph;
                entityJson.parentEntity = entity;
                entityJson.parse(entityJsonValue);
            }
        }

        String parentInternalNodeId = jsonValue.getString("parentInternalNodeId", null);

        TransformJson transformJson = new TransformJson();

        if (jsonValue.has("transform")) {
            transformJson.parse(jsonValue.get("transform"));

            NodeComponent nodeComponent = nhg.entities.getComponent(entity, NodeComponent.class);
            nodeComponent.parentInternalNodeId = parentInternalNodeId;
            nodeComponent.setTransform(
                    transformJson.position,
                    transformJson.rotation,
                    transformJson.scale);
        }

        output = entity;
    }

    public void setParentEntity(int parentEntity) {
        this.parentEntity = parentEntity;
    }

    public void setSceneGraph(SceneGraph sceneGraph) {
        this.sceneGraph = sceneGraph;
    }

    @Override
    public Integer get() {
        return output;
    }
}
