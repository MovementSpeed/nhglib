package io.github.voidzombie.nhglib.graphics.worlds.base;

import io.github.voidzombie.nhglib.graphics.scenes.Scene;

/**
 * Created by Fausto Napoli on 28/12/2016.
 */
public interface NHGWorld {
    void addScene(Scene scene);
    void loadScene(String name);
    void unloadScene(String name);
    void setBounds();
    void update();
    Scene getScene(String name);
}
