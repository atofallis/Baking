package com.tofallis.baking;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pool for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class DiskIOExecutor {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static DiskIOExecutor sInstance;
    private final Executor diskIO;

    private DiskIOExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static void execute(Runnable r) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DiskIOExecutor(Executors.newSingleThreadExecutor());
            }
        }
        sInstance.diskIO.execute(r);
    }
}
