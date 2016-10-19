package io.github.voidzombie.nhglib.utils;

import com.badlogic.gdx.Gdx;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Logger {
    public static void log(Object caller, String message) {
        Gdx.app.log(caller.getClass().getSimpleName(), message);
    }
}
