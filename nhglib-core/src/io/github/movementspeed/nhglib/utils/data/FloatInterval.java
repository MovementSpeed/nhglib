package io.github.movementspeed.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
public class FloatInterval {
    private float a;
    private float b;

    public FloatInterval(float a, float b) {
        this.a = a;
        this.b = b;
    }

    public void setA(float a) {
        this.a = a;
    }

    public void setB(float b) {
        this.b = b;
    }

    public boolean inRange(float value) {
        return inRange(value, true);
    }

    public boolean inRange(float value, boolean inclusive) {
        boolean res;

        if (inclusive) {
            res = value <= b && value >= a;
        } else {
            res = value < b && value > a;
        }

        return res;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }
}
