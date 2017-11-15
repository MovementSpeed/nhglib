package io.github.movementspeed.nhglib.utils.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by Fausto Napoli on 15/12/2016.
 */
public class GLUtils {
    public static void clearScreen() {
        clearScreen(Color.BLACK);
    }

    public static void clearScreen(Color color) {
        clearScreen(color, GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public static void clearScreen(Color color, int mask) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(mask);
    }

    public static void setViewport(int x, int y, int width, int height) {
        Gdx.gl.glViewport(x, y, width, height);
    }

    public static void setViewport(int width, int height) {
        setViewport(0, 0, width, height);
    }

    public static boolean isFloatTextureSupported() {
        return !Gdx.graphics.supportsExtension("OES_texture_float") && Gdx.app.getType() != Application.ApplicationType.Desktop;
    }
}
