package io.github.movementspeed.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
public class IntegerInterval {
    private int a;
    private int b;

    public IntegerInterval(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public boolean inRange(int value) {
        return inRange(value, true);
    }

    public boolean inRange(int value, boolean inclusive) {
        boolean res;

        if (inclusive) {
            res = value <= b && value >= a;
        } else {
            res = value < b && value > a;
        }

        return res;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}
