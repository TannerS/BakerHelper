package io.dev.tanners.bakerhelper.aac.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import io.dev.tanners.bakerhelper.aac.db.config.DBConfig;
import io.dev.tanners.bakerhelper.model.Recipe;

@Database(entities = {Recipe.class}, version = DBConfig.DATABASE_VERISON, exportSchema = false)
@TypeConverters({ListIngredientConverter.class, ListStepConverter.class})
public abstract class RecipeDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static RecipeDatabase mInstance;

    public static RecipeDatabase getInstance(Context context) {
        // check if instance already exist
        if (mInstance == null) {
            // used for synchronization of threads by locking this code chunk
            // read more: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
            synchronized (LOCK) {
                // just in case other threads were waiting for the lock to unlock
                if (mInstance == null) {
                    // create database instance using Room
                    mInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RecipeDatabase.class,
                            DBConfig.DATABASE_NAME
                    ).build();
                }
            }
        }
        // return current instance
        return mInstance;
    }

    public abstract RecipeDao getRecipeDao();

}