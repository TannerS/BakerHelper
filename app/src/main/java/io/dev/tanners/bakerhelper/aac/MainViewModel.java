package io.dev.tanners.bakerhelper.aac;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.model.Recipe;

public class MainViewModel extends AndroidViewModel {
    // list of recipes
    private MutableLiveData<List<Recipe>> mRecipes;
//    private Application application;
    private RecipeRepository mRecipeRepository;

    /**
     * constructor
     *
     * @param application
     * @param mRecipeRepository
     */
    public MainViewModel(@NonNull Application application, RecipeRepository mRecipeRepository) {
        super(application);
//        this.application = application;
        this.mRecipeRepository = mRecipeRepository;
        // pull new data on each viewmodel init
        // this SHOULD be one network call per app start
        // well rest is cached here
        this.mRecipeRepository.pullNewData();
        Log.i("VIEWMODEL", "CONSTRUCTOR");
    }

    /**
     * get recipes
     *
     * @return
     */
    public LiveData<List<Recipe>> getmRecipes() throws IOException {
        Log.i("VIEWMODEL", "GET DATA");
        return mRecipeRepository.getRecipes();
    }

//    public void pullNewData()
//    {
//        mRecipeRepository.pullNewData();
//        mRecipes
//    }
}

