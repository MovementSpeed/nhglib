package io.github.movementspeed.nhglib.utils.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Field;

/**
 * Small utility class for manually tweaking values in runtime. It is designed to be used only for prototyping and
 * removed from project code-base once it's no longer needed. Once a numeric class field is registered for tweaking
 * (using one of tweak static methods), the value that field can be increased or decreased by keyboard while the
 * application is running.
 * <p>
 * The reflection is used, so field can be private, but it must not be final.
 * Keep in mind that reference to passed object is kept, so GC will not be able to free any object with registered tweak field.
 */
public class ParameterTweaker extends InputAdapter {
    static public Input.Keys KEYS;
    static private ParameterTweaker instance;
    static private IntMap<Array<Tweak>> tweakMap = new IntMap<Array<Tweak>>(2);

    static class Tweak {
        Object caller;
        Field field;
        float step;
        float min;
        float max;

        Tweak(Object caller, Field field, float step, float min, float max) {
            this.caller = caller;
            this.field = field;
            this.step = step;
            this.min = min;
            this.max = max;
        }
    }

    /**
     * Register class field for runtime tweaking.
     * This also creates a static reference to passed object, so GC will not be able to free it.
     *
     * @param objInstance object instance that holds the field (usually "this" keyword can be used)
     * @param fieldName   name of class field
     * @param step        by how much should value change on key press
     * @param min         minimum value
     * @param max         maximum value
     * @param decreaseKey ID of key used to decrease value
     * @param increaseKey ID of key used to increase value
     */
    static public void tweak(Object objInstance, String fieldName, float step, float min, float max, int decreaseKey, int increaseKey) {
        Field field = null;
        try {
            field = objInstance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (!(field.getType().equals(Float.TYPE) || field.getType().equals(Integer.TYPE)))
            throw new NotImplementedException();

        field.setAccessible(true);

        // add new tweak command to decrease key mapping
        Array<Tweak> keyTweaks = tweakMap.get(decreaseKey, new Array<Tweak>(1));
        keyTweaks.add(new Tweak(objInstance, field, -step, min, max));
        tweakMap.put(decreaseKey, keyTweaks);
        Gdx.app.log("ParameterTweaker", String.format("Key id %d set to change parameter %s by %+f between min=%f and max=%f",
                decreaseKey, fieldName, -step, min, max));

        // add new tweak command to increase key mapping
        keyTweaks = tweakMap.get(increaseKey, new Array<Tweak>(1));
        keyTweaks.add(new Tweak(objInstance, field, +step, min, max));
        tweakMap.put(increaseKey, keyTweaks);
        Gdx.app.log("ParameterTweaker", String.format("Key id %d set to change parameter %s by %+f between min=%f and max=%f",
                increaseKey, fieldName, +step, min, max));

        if (instance == null) {
            instance = new ParameterTweaker();
            Gdx.input.setInputProcessor(instance);
        }
    }

    /**
     * Register class field for runtime tweaking using + and - keys.
     * This also creates a static reference to passed object, so GC will not be able to free it.
     *
     * @param objInstance object instance that holds the field (usually "this" keyword can be used)
     * @param fieldName   name of class field
     * @param step        by how much should value change on key press
     * @param min         minimum value
     * @param max         maximum value
     */
    static public void tweak(Object objInstance, String fieldName, float step, float min, float max) {
        tweak(objInstance, fieldName, step, min, max, KEYS.PLUS, KEYS.MINUS);
    }

    /**
     * Register class field for runtime tweaking using + and - keys.
     * This also creates a static reference to passed object, so GC will not be able to free it.
     *
     * @param objInstance object instance that holds the field (usually "this" keyword can be used)
     * @param fieldName   name of class field
     * @param step        by how much should value change on key press
     */
    static public void tweak(Object objInstance, String fieldName, float step) {
        tweak(objInstance, fieldName, step, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    /**
     * Register class field for runtime tweaking.
     * This also creates a static reference to passed object, so GC will not be able to free it.
     *
     * @param objInstance object instance that holds the field (usually "this" keyword can be used)
     * @param fieldName   name of class field
     * @param step        by how much should value change on key press
     * @param decreaseKey ID of key used to decrease value
     * @param increaseKey ID of key used to increase value
     */
    static public void tweak(Object objInstance, String fieldName, float step, int decreaseKey, int increaseKey) {
        tweak(objInstance, fieldName, step, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, decreaseKey, increaseKey);
    }

    /**
     * Register class field for runtime tweaking using + and - keys.
     *
     * @param objInstance object instance that holds the field (usually "this" keyword can be used)
     * @param fieldName   name of class field
     */
    static public void tweak(Object objInstance, String fieldName) {
        tweak(objInstance, fieldName, 1f);
    }

    private boolean changeValue(Tweak tweak) {
        float fieldValue = 0f;
        boolean ret = false;
        try {
            if (tweak.field.getType().equals(Float.TYPE))
                fieldValue = tweak.field.getFloat(tweak.caller);
            else if (tweak.field.getType().equals(Integer.TYPE))
                fieldValue = tweak.field.getInt(tweak.caller);
            else
                assert false;
            fieldValue = Math.min(Math.max(fieldValue + tweak.step, tweak.min), tweak.max);
            if (tweak.field.getType().equals(Float.TYPE)) {
                tweak.field.setFloat(tweak.caller, fieldValue);
                ret = true;
            } else if (tweak.field.getType().equals(Integer.TYPE)) {
                tweak.field.setInt(tweak.caller, Math.round(fieldValue));
                ret = true;
            }
            if (ret)
                Gdx.app.log("ParameterTweaker", String.format("Changed value %s to %f", tweak.field.getName(), fieldValue));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean keyDown(int i) {
        Array<Tweak> keyTweaks = tweakMap.get(i);
        if (keyTweaks != null) {
            for (int j = keyTweaks.size - 1; j >= 0; j--) {
                Tweak tweak = keyTweaks.get(j);
                changeValue(tweak);
            }
            return true;
        } else
            return false;
    }
}