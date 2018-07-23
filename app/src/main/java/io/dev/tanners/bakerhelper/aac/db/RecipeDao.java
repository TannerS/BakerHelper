package io.dev.tanners.bakerhelper.aac.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.aac.db.config.DBConfig;

/**
 * db dao
 */
@Dao
public interface RecipeDao {

    /*
     * get all recipes
     *
     * @return
     */
    @Query(DBConfig.GET_ALL_RECIPE_QUERY)
    List<Recipe> loadAllRecipes();

    /**
     * get recipes
     *
     * @param mRecipe
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecipe(Recipe mRecipe);

    /**
     * insert list of recipes
     *
     * @param mRecipe
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecipes(List<Recipe> mRecipe);

    /**
     * update recipe
     *
     * @param mRecipe
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(Recipe mRecipe);

    /**
     * delete recipe
     *
     * @param mRecipe
     */
    @Delete
    void deleteRecipe(Recipe mRecipe);

    /**
     * get recipe by id
     *
     * @param id
     * @return
     */
    @Query(DBConfig.GET_RECIPE_BY_ID_QUERY)
    Recipe loadRecipeById(int id);
}



