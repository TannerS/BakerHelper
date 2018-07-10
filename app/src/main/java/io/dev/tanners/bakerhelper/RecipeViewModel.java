package io.dev.tanners.bakerhelper;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import io.dev.tanners.bakerhelper.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.recipe.StepWrapper;

public class RecipeViewModel extends AndroidViewModel {
//    private LiveData<List<Step>> mStep;
    private List<StepWrapper> mStep;
    private List<Ingredient> mIngredient;
    // this is an array but I will convert it to a string for UI
//    private String mIngredients;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mStep = null;
    }

    public List<StepWrapper> getmStep() {
        return mStep;
    }

    public void setmStep(List<StepWrapper> mStep) {
        this.mStep = mStep;
    }

    public List<Ingredient> getmIngredient() {
        return mIngredient;
    }

    public void setmIngredient(List<Ingredient> mIngredient) {
        this.mIngredient = mIngredient;
    }
}

