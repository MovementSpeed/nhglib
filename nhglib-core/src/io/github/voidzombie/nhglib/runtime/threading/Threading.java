package io.github.voidzombie.nhglib.runtime.threading;

import java.util.concurrent.*;

/**
 * Created by Fausto Napoli on 05/11/2016.
 */
public class Threading {
    public final static int cores = Runtime.getRuntime().availableProcessors();
    private ExecutorService executor;
    private CountDownLatch latch;

    public Threading() {
        executor = Executors.newFixedThreadPool(cores);
    }

    public void execute(Work work) {
        executor.execute(work);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public void await(int tasks) {
        latch = new CountDownLatch(tasks);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void countDown() {
        try {
            latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
