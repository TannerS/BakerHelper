package io.dev.tanners.bakerhelper.aac.db.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.db.db.config.DBConfig;
import io.dev.tanners.bakerhelper.model.Recipe;

@Dao
public interface RecipeDao {

    @Query(DBConfig.GET_ALL_RECIPE_QUERY)
    LiveData<List<Recipe>> loadAllRecipes();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecipe(Recipe mRecipe);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(Recipe mRecipe);


    @Delete
    void deleteRecipe(Recipe mRecipe);


    @Query(DBConfig.GET_RECIPE_BY_ID_QUERY)
    Recipe loadRecipeById(int id);
}



