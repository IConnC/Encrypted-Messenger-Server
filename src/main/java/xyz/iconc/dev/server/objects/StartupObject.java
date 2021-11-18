package xyz.iconc.dev.server.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public abstract class StartupObject implements IReady {
    protected AtomicBoolean readyState;
    protected List<CountDownLatch> latchList;
    protected ReentrantLock latchListLock; // Prevents latchList from being modified by different threads simultaneously


    public StartupObject() {
        readyState = new AtomicBoolean(false);
        latchListLock = new ReentrantLock();
        latchList = new ArrayList<>();
    }

    /**
     * Counts down latch when object is ready
     *
     * @param latch     Latch which will be counted down when current object is ready
     */
    public void registerLatch(CountDownLatch latch) {
        if (isReady()) {
            latch.countDown();
            return;
        }
        latchListLock.lock();
        latchList.add(latch);
        latchListLock.unlock();
    }

    @Override
    public boolean isReady() {
        return readyState.get();
    }
}
