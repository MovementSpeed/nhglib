package io.github.movementspeed.nhglib.utils.math;

import io.github.movementspeed.nhglib.utils.data.FloatInterval;

/**
 * Created by Fausto Napoli on 12/03/2017.
 */
public class NhgMath {
    private static FloatInterval tempFrom = new FloatInterval(0f, 0f);
    private static FloatInterval tempTo = new FloatInterval(0f, 0f);

    public static float normalize(float value, FloatInterval from, FloatInterval to) {
        tempFrom.setA(from.getA());
        tempFrom.setB(from.getB());

        tempTo.setA(to.getA());
        tempTo.setB(to.getB());

        return (tempTo.getB() - tempTo.getA()) / (tempFrom.getB() - tempFrom.getA()) * (value - tempFrom.getB()) + tempTo.getB();
    }
}
