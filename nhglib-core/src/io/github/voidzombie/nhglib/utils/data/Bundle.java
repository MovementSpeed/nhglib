package io.github.voidzombie.nhglib.utils.data;

import java.util.HashMap;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Bundle extends HashMap<String, Object> {
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean res = null;
        Object value = get(key);

        if (value instanceof Boolean) {
            res = (Boolean) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Byte getByte(String key) {
        return getByte(key, null);
    }

    public Byte getByte(String key, Byte defaultValue) {
        Byte res = null;
        Object value = get(key);

        if (value instanceof Byte) {
            res = (Byte) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Short getShort(String key) {
        return getShort(key, null);
    }

    public Short getShort(String key, Short defaultValue) {
        Short res = null;
        Object value = get(key);

        if (value instanceof Short) {
            res = (Short) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Integer res = null;
        Object value = get(key);

        if (value instanceof Integer) {
            res = (Integer) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Long res = null;
        Object value = get(key);

        if (value instanceof Long) {
            res = (Long) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Float getFloat(String key, Float defaultValue) {
        Float res = null;
        Object value = get(key);

        if (value instanceof Float) {
            res = (Float) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        Double res = null;
        Object value = get(key);

        if (value instanceof Double) {
            res = (Double) value;
        }

        if (res == null) {
            res = defaultValue;
        }

        return res;
    }

    public Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    public Character getCharacter(String key, Character defaultValue) {
        Character res = null;
        Object value = get(key);

        if (value instanceof Character) {
            res = (Character) value;
        }

        if (res == null) {
            res = defaultValue;
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
