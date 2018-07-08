package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.recipe.StepWrapper;
import io.dev.tanners.bakerhelper.recipe.StepsExpandableAdapter;

public class RecipeActivity extends AppCompatActivity {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        getData();
        setUpExpandableList();
    }

    private void setUpExpandableList()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipe_details_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //instantiate your adapter with the list of genres
        StepsExpandableAdapter mAdapter = new StepsExpandableAdapter(this, mRecipeViewModel.getmStep());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private List<StepWrapper> setUpExpandableListData(List<Step> mSteps)
    {
        List<StepWrapper> mStepsExtra = new ArrayList<StepWrapper>();

        for(final Step mStep : mSteps)
        {
            mStepsExtra.add(
                    new StepWrapper(
                        mStep.getShortDescription(),
                        new ArrayList<Step>() {{
                            add(mStep);
                        }}
                    )
            );

        }

        return mStepsExtra;
    }

    private void getData()
    {
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        Recipe mRecipe = getIntent().getParcelableExtra(RECIPE_DATA);
        mRecipeViewModel.setmIngredient(mRecipe.getIngredients());
        List<StepWrapper> mStepsExtra = setUpExpandableListData(mRecipe.getSteps());
        mRecipeViewModel.setmStep(mStepsExtra);
    }
}
