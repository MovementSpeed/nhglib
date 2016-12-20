package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.graphics.scenes.SceneGraph;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class EntityJson implements JsonParseable<Integer> {
    public Integer parentEntity;
    public SceneGraph sceneGraphRef;
    public TransformJson transform;
    public Array<EntityJson> entities;

    private Integer output;

    public EntityJson() {
        transform = new TransformJson();
        entities = new Array<>();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        transform.parse(jsonValue.get("transform"));
        int entity = sceneGraphRef.addSceneEntity(parentEntity);

        JsonValue componentsJson = jsonValue.get("components");

        for (JsonValue component : componentsJson) {
            ComponentJson componentJson = new ComponentJson();
            componentJson.entity = entity;
            componentJson.parse(component);
        }

        JsonValue entitiesJson = jsonValue.get("entities");

        for (JsonValue entityJsonValue : entitiesJson) {
            EntityJson entityJson = new EntityJson();
            entityJson.sceneGraphRef = sceneGraphRef;
            entityJson.parentEntity = entity;
            entityJson.parse(entityJsonValue);

            entities.add(entityJson);
        }
    }

    @Override
    public Integer get() {
        return output;
    }
}
