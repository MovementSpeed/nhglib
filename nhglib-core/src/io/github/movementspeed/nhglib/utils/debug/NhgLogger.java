package io.github.movementspeed.nhglib.utils.debug;

import com.badlogic.gdx.Gdx;
import io.github.movementspeed.nhglib.Nhg;

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
public class NhgLogger {
    public static void log(Object caller, String message) {
        if (Nhg.debugLogs && canLog()) {
            Gdx.app.log(getCallerString(caller), message);
        }
    }

    public static void log(Object caller, String message, Object... objects) {
        if (Nhg.debugLogs && canLog()) {
            String formattedMessage = String.format(message, objects);
            Gdx.app.log(getCallerString(caller), formattedMessage);
        }
    }

    private static boolean canLog() {
        return true;
    }

    private static String getCallerString(Object caller) {
        String callerString;

        if (caller instanceof String) {
            callerString = (String) caller;
        } else {
            callerString = caller.getClass().getName();
            int lastIndexOfDot = callerString.lastIndexOf(".");
            callerString = callerString.substring(lastIndexOfDot + 1, callerString.length());
        }

        return callerString;
    }
}
