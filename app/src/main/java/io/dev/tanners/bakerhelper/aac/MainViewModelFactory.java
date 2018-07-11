package io.dev.tanners.bakerhelper.aac;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.io.IOException;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private RecipeRepository mRecipeRepository;
    private Application application;

    public MainViewModelFactory(Application application, RecipeRepository mRecipeRepository){
        this.application = application;
        this.mRecipeRepository = mRecipeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(application, mRecipeRepository);
    }
}