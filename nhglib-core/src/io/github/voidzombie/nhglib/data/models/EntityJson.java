package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.graphics.scenes.SceneGraph;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class EntityJson implements JsonParseable<Integer> {
    private Integer parentEntity;
    private Integer output;
    private SceneGraph sceneGraphRef;

    @Override
    public void parse(JsonValue jsonValue) {
        TransformJson transformJson = new TransformJson();
        transformJson.parse(jsonValue.get("transform"));

        int entity = sceneGraphRef.addSceneEntity(parentEntity);

        NodeComponent nodeComponent = NHG.entitySystem.getComponent(entity, NodeComponent.class);

        if (transformJson.type == TransformJson.Type.ABSOLUTE) {
            nodeComponent.setTranslation(transformJson.position);
            nodeComponent.setRotation(transformJson.rotation);
            nodeComponent.setScale(transformJson.scale);
        } else if (transformJson.type == TransformJson.Type.RELATIVE) {
            NodeComponent parentNodeComponent =
                    NHG.entitySystem.getComponent(parentEntity, NodeComponent.class);

            nodeComponent.setTranslation(parentNodeComponent.getTranslation());
            nodeComponent.setRotation(parentNodeComponent.getRotation());
            nodeComponent.setScale(parentNodeComponent.getScale());

            nodeComponent.translate(transformJson.position);
            nodeComponent.rotate(transformJson.rotation);
            nodeComponent.scale(transformJson.scale);
        }

        JsonValue componentsJson = jsonValue.get("components");

        for (JsonValue componentJsonValue : componentsJson) {
            String type = componentJsonValue.getString("type");
            ComponentJson componentJson = SceneUtils.getInstance().componentJsonFromType(type);

            if (componentJson != null) {
                componentJson.entity = entity;
                componentJson.parse(componentJsonValue);
            }
        }

        JsonValue entitiesJson = jsonValue.get("entities");

        for (JsonValue entityJsonValue : entitiesJson) {
            EntityJson entityJson = new EntityJson();
            entityJson.sceneGraphRef = sceneGraphRef;
            entityJson.parentEntity = entity;
            entityJson.parse(entityJsonValue);
        }

        output = entity;
    }

    public void setParentEntity(Integer parentEntity) {
        this.parentEntity = parentEntity;
    }

    public void setSceneGraph(SceneGraph sceneGraph) {
        this.sceneGraphRef = sceneGraph;
    }

    @Override
    public Integer get() {
        return output;
    }
}
