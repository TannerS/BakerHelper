package io.dev.tanners.bakerhelper;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import io.dev.tanners.bakerhelper.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.model.Recipe;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> mRecipes;

    /**
     * Constructor
     *
     * @param application
     */
    public MainViewModel(@NonNull Application application) {
        super(application);
        loadInitData();
    }

    public LiveData<List<Recipe>> getmRecipes() {
        return mRecipes;
    }

    public void setmRecipes(LiveData<List<Recipe>> mRecipes) {
        this.mRecipes = mRecipes;
    }

    private void loadInitData()
    {
        RecipeDatabase mRecipeDatabase = RecipeDatabase.getInstance(this.getApplication());
        mRecipes = mRecipeDatabase.getRecipeDao().loadAllRecipes();
    }
}

