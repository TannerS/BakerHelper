package io.dev.tanners.bakerhelper.aac;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Recipe;

public class MainViewModel extends AndroidViewModel {
    // list of recipes
    private LiveData<List<Recipe>> mRecipes;

    /**
     * constructor
     *
     * @param application
     * @param mRecipeRepository
     */
    public MainViewModel(@NonNull Application application, RecipeRepository mRecipeRepository) {
        super(application);

        try {
            // use repo to get data
            mRecipes = mRecipeRepository.getAllRecipes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get recipes
     *
     * @return
     */
    public LiveData<List<Recipe>> getmRecipes() {
        return mRecipes;
    }
}

