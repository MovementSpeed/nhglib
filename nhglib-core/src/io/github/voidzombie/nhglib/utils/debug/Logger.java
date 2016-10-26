package io.github.voidzombie.nhglib.utils.debug;

import com.badlogic.gdx.Gdx;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class Logger {
    public void log(Object caller, String message) {
        Gdx.app.log(getCallerString(caller), message);
    }

    public void log(Object caller, String message, Object... objects) {
        String formattedMessage = String.format(message, objects);
        Gdx.app.log(getCallerString(caller), formattedMessage);
    }

    private String getCallerString(Object caller) {
        String callerString = caller.getClass().getName();
        Integer lastIndexOfDot = callerString.lastIndexOf(".");

        return callerString.substring(lastIndexOfDot + 1, callerString.length());
    }
}
