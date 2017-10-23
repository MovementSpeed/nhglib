package io.github.movementspeed.nhglib.core.threading;

import com.badlogic.gdx.utils.ArrayMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Fausto Napoli on 05/11/2016.
 */
public class Threading {
    public final static int cores = Runtime.getRuntime().availableProcessors();

    private ExecutorService executor;
    private ArrayMap<Integer, ResettableCountDownLatch> latches;

    public Threading() {
        executor = Executors.newFixedThreadPool(cores);
        latches = new ArrayMap<>();
    }

    public void execute(Work work) {
        executor.execute(work);
    }

    public void createLatch(int latchId, int count) {
        latches.put(latchId, new ResettableCountDownLatch(count));
    }

    public void awaitLatch(int latchId) {
        if (latches.containsKey(latchId)) {
            ResettableCountDownLatch latch = latches.get(latchId);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            latch.reset();
        }
    }

    public void countDownLatch(int latchId) {
        if (latches.containsKey(latchId)) {
            latches.get(latchId).countDown();
        }
    }

    public void setLatchCount(int latchId, int count) {
        createLatch(latchId, count);
    }

    public void terminate() {
        executor.shutdown();
    }
}