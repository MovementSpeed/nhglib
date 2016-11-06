package io.github.voidzombie.nhglib.runtime.threading;

import java.util.concurrent.*;

/**
 * Created by Fausto Napoli on 05/11/2016.
 */
public class Threading {
    public final static int cores = Runtime.getRuntime().availableProcessors();

    private ExecutorService executor;
    private ResettableCountDownLatch latch;

    public Threading() {
        executor = Executors.newFixedThreadPool(cores);
        latch = new ResettableCountDownLatch(cores);
    }

    public void execute(Work work) {
        executor.execute(work);
    }

    public void await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.reset();
    }

    public void countDown() {
        latch.countDown();
    }

    public void setLatchCount(int count) {
        latch = new ResettableCountDownLatch(count);
    }
}