package io.dev.tanners.bakerhelper.aac;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // repo to get the data
    private RecipeRepository mRecipeRepository;
    private Application application;

    /**
     * @param application
     * @param mRecipeRepository
     */
    public MainViewModelFactory(Application application, RecipeRepository mRecipeRepository){
        this.application = application;
        this.mRecipeRepository = mRecipeRepository;
    }

    /**
     * @param modelClass
     * @param <T>
     * @return
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(application, mRecipeRepository);
    }
}