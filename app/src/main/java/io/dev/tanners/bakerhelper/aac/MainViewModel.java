package io.dev.tanners.bakerhelper.aac;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Recipe;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> mRecipes;

    public MainViewModel(@NonNull Application application, RecipeRepository mRecipeRepository) {
        super(application);
        try {
            mRecipes = mRecipeRepository.getAllRecipes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<Recipe>> getmRecipes() {
        return mRecipes;
    }
}

