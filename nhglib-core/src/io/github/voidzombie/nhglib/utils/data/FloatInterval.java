package io.github.voidzombie.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
public class FloatInterval {
    private Float a;
    private Float b;

    public FloatInterval(Float a, Float b) {
        this.a = a;
        this.b = b;
    }

    public void setA(Float a) {
        this.a = a;
    }

    public void setB(Float b) {
        this.b = b;
    }

    public Boolean inRange(Float value) {
        return inRange(value, true);
    }

    public Boolean inRange(Float value, Boolean inclusive) {
        Boolean res;

        if (a != null && b != null) {
            if (inclusive) {
                res = value <= b && value >= a;
            } else {
                res = value < b && value > a;
            }
        } else {
            res = false;
        }

        return res;
    }

    public Float getA() {
        return a;
    }

    public Float getB() {
        return b;
    }
}
