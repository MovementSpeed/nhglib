package io.github.movementspeed.nhglib.utils.data;

import java.util.HashMap;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Bundle extends HashMap<String, Object> {
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean res = defaultValue;
        Object value = get(key);

        if (value instanceof Boolean) {
            res = (boolean) value;
        }

        return res;
    }

    public byte getByte(String key) {
        return getByte(key, (byte) -1);
    }

    public byte getByte(String key, byte defaultValue) {
        byte res = defaultValue;
        Object value = get(key);

        if (value instanceof Byte) {
            res = (byte) value;
        }

        return res;
    }

    public short getShort(String key) {
        return getShort(key, (short) -1);
    }

    public short getShort(String key, short defaultValue) {
        short res = defaultValue;
        Object value = get(key);

        if (value instanceof Short) {
            res = (short) value;
        }

        return res;
    }

    public int getInteger(String key) {
        return getInteger(key, -1);
    }

    public int getInteger(String key, int defaultValue) {
        int res = defaultValue;
        Object value = get(key);

        if (value instanceof Integer) {
            res = (int) value;
        }

        return res;
    }

    public long getLong(String key) {
        return getLong(key, -1L);
    }

    public long getLong(String key, long defaultValue) {
        long res = defaultValue;
        Object value = get(key);

        if (value instanceof Long) {
            res = (long) value;
        }

        return res;
    }

    public float getFloat(String key) {
        return getFloat(key, -1f);
    }

    public float getFloat(String key, float defaultValue) {
        float res = defaultValue;
        Object value = get(key);

        if (value instanceof Float) {
            res = (float) value;
        }

        return res;
    }

    public double getDouble(String key) {
        return getDouble(key, -1D);
    }

    public double getDouble(String key, double defaultValue) {
        double res = defaultValue;
        Object value = get(key);

        if (value instanceof Double) {
            res = (double) value;
        }

        return res;
    }

    public char getCharacter(String key) {
        return getCharacter(key, ' ');
    }

    public char getCharacter(String key, char defaultValue) {
        char res = defaultValue;
        Object value = get(key);

        if (value instanceof Character) {
            res = (char) value;
        }

        return res;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        String res = null;
        Object value = get(key);

        if (value instanceof String) {
            res = (String) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }
}
