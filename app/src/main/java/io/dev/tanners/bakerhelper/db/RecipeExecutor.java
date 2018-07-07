package io.dev.tanners.bakerhelper.db;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Used as a singletone device to work on a thread for db functionality
 */
public class RecipeExecutor {
    // lock to help prevent race conditions
    private static final Object LOCK = new Object();
    // self static object
    private static RecipeExecutor mRecipeExecutor;
    // executor for disk
    private final Executor mDiskIO;

    /**
     * Constructor
     *
     * @param diskIO
     */
    private RecipeExecutor(Executor diskIO) {
        this.mDiskIO = diskIO;
    }

    /**
     * Get new or existing instance of RecipeExecutor
     * @return
     */
    public static RecipeExecutor getInstance() {
        // check for existing instance of executor
        if (mRecipeExecutor == null) {
            // used to prevent multiple threads from race conditions
            synchronized (LOCK) {
                // if multiple threads fight to create executor, make sure we
                // multiple threads can be waiting at this lock
                // we must make sure they don't recreate it after
                // that first thread got finished creating it
                if (mRecipeExecutor == null) {
                    mRecipeExecutor = new RecipeExecutor(
                        // get single thread executor
                        Executors.newSingleThreadExecutor()
                    );
                }
            }
        }
        // return instance
        return mRecipeExecutor;
    }

    /**
     * Get disk executor
     *
     * @return
     */
    public Executor mDiskIO() {
        return mDiskIO;
    }
}