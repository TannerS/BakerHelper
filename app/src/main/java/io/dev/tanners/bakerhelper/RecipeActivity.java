package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.recipe.IngredientAdapter;
import io.dev.tanners.bakerhelper.recipe.StepWrapper;
import io.dev.tanners.bakerhelper.recipe.StepsExpandableAdapter;

public class RecipeActivity extends AppCompatActivity {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        setUpViewModel();
        getData();
    }

    private void setUpExpandableStepList(List<StepWrapper> mSteps)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recipe_steps);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        StepsExpandableAdapter mAdapter = new StepsExpandableAdapter(this, mSteps);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpIngredientList(List<Ingredient> mIngredients)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recipe_ingredients);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        IngredientAdapter mAdapter = new IngredientAdapter(this, mIngredients);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void setUpViewModel()
    {
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
    }

    private List<StepWrapper> setUpExpandableListData(List<Step> mSteps)
    {
        List<StepWrapper> mStepsExtra = new ArrayList<StepWrapper>();
        int mStepCounter = 0;

        for(final Step mStep : mSteps)
        {
            mStepsExtra.add(
                    new StepWrapper(
                        "Step " + mStepCounter + ": " + mStep.getShortDescription(),
                        new ArrayList<Step>() {{
                            add(mStep);
                        }}
                    )
            );

            mStepCounter++;
        }

        return mStepsExtra;
    }

    private void getData()
    {
        if(getIntent() != null && getIntent().hasExtra(RECIPE_DATA))
        {
            Recipe mRecipe = getIntent().getParcelableExtra(RECIPE_DATA);

            if(mRecipeViewModel.getmStep() == null) {
                List<StepWrapper> mStepsExtra = setUpExpandableListData(mRecipe.getSteps());
                mRecipeViewModel.setmStep(mStepsExtra);
            }

            if(mRecipeViewModel.getmIngredient() == null)
            {
                mRecipeViewModel.setmIngredient(mRecipe.getIngredients());
            }

            setUpExpandableStepList(mRecipeViewModel.getmStep());

            setUpIngredientList(mRecipeViewModel.getmIngredient());
        }
    }


}
