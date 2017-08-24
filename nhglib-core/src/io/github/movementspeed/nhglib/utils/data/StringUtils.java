package io.github.movementspeed.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class StringUtils {
    public static int idFromString(String string) {
        return string.hashCode();
    }
}
