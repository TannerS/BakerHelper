package io.dev.tanners.bakerhelper.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import io.dev.tanners.bakerhelper.db.config.DBConfig;
import io.dev.tanners.bakerhelper.db.support.IngredientConverter;
import io.dev.tanners.bakerhelper.db.support.StepConverter;
import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;

@Database(
        entities = {Recipe.class, Ingredient.class, Step.class},
        version = DBConfig.DATABASE_VERISON,
        exportSchema = false
)
@TypeConverters({StepConverter.class, IngredientConverter.class})
public abstract class RecipeDatabase extends RoomDatabase {
    // used for synchronization
    private static final Object LOCK = new Object();
    // name of database
    private static RecipeDatabase mDbInstance;

    /**
     * Get instance of database
     * @param context
     * @return
     */
    public static RecipeDatabase getInstance(Context context) {
        // check if instance already exist
        if (mDbInstance == null) {
            // used for synchronization of threads by locking this code chunk
            // read more: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
            synchronized (LOCK) {
                // if multiple threads fight to create db, make sure we
                // multiple threads can be waiting at this lock
                // we must make sure they don't recreate it after
                // that first thread got finished creating it
                if (mDbInstance == null) {
                    // create database instance using Room
                    mDbInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RecipeDatabase.class,
                            DBConfig.DATABASE_NAME
                    ).build();
                }
            }
        }
        // return current instance
        return mDbInstance;
    }

    public abstract RecipeDao getRecipeDao();
}