package io.github.movementspeed.tests.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.enums.OpenGLVersion;
import io.github.movementspeed.tests.Main;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.useGL30 = true;
        Nhg.glVersion = OpenGLVersion.VERSION_3;

        new LwjglApplication(new Main(), config);
    }
}
