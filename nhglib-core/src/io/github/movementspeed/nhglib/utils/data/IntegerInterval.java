package io.github.movementspeed.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
public class IntegerInterval {
    private Integer a;
    private Integer b;

    public IntegerInterval(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Boolean inRange(Integer value) {
        return inRange(value, true);
    }

    public Boolean inRange(Integer value, Boolean inclusive) {
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

    public Integer getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }
}
