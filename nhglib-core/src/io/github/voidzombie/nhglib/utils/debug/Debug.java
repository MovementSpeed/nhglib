package io.github.voidzombie.nhglib.utils.debug;

import com.badlogic.gdx.utils.PerformanceCounters;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class Debug {
    public final static PerformanceCounters performanceCounters;

    static {
        performanceCounters = new PerformanceCounters();
    }
}
