package io.github.voidzombie.nhglib.graphics.scenes;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class Scene {
    public String name;
    public SceneGraph sceneGraph;

    public Array<Asset> assets;

    public Scene(Nhg nhg, String rootId) {
        sceneGraph = new SceneGraph(nhg, rootId);
        assets = new Array<>();
    }
}
