package io.github.movementspeed.tests.launchers;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.enums.OpenGLVersion;
import io.github.movementspeed.tests.Main;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useOpenGL3(true, 3, 2);
        config.setTitle("Desktop Test");
        config.setWindowedMode(1280, 720);
        Nhg.glVersion = OpenGLVersion.VERSION_3;

        new Lwjgl3Application(new Main(), config);
    }
}