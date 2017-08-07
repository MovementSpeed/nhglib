package io.github.voidzombie.tests.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.voidzombie.tests.Main;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.depth = 32;

        new LwjglApplication(new Main(), config);
    }
}
