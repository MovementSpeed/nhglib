package io.github.voidzombie.nhglib.utils.scenes;

import com.artemis.Component;
import io.github.voidzombie.nhglib.NHG;

import java.util.HashMap;

/**
 * Created by Fausto Napoli on 20/12/2016.
 */
public class SceneUtils {
    private HashMap<String, Class<? extends Component>> componentClassesMapping;

    public SceneUtils() {
        componentClassesMapping = new HashMap<>();
    }

    public Component componentFromType(int entity, String type) {
        Component res = null;
        Class componentClass = componentClassFromType(type);

        if (componentClass != null) {
            res = NHG.entitySystem.createComponent(entity, componentClass);
        }

        return res;
    }

    public void addMapping(String type, Class<? extends Component> componentClass) {
        componentClassesMapping.put(type, componentClass);
    }

    public Class<? extends Component> componentClassFromType(String type) {
        return componentClassesMapping.get(type);
    }
}
