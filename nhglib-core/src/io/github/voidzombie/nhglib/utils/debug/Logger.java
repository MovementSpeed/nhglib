package io.github.voidzombie.nhglib.utils.debug;

import com.badlogic.gdx.Gdx;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Logger {
    public static void log(Object caller, String message) {
        Gdx.app.log(caller.getClass().getSimpleName(), message);
    }

    public static void log(Object caller, String message, Object... objects) {
        String formattedMessage = String.format(message, objects);
        Gdx.app.log(caller.getClass().getSimpleName(), formattedMessage);
    }
}
