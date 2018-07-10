package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.recipe.IngredientAdapter;
import io.dev.tanners.bakerhelper.recipe.StepWrapper;
import io.dev.tanners.bakerhelper.recipe.StepsExpandableAdapter;
import io.dev.tanners.bakerhelper.util.ImageDisplay;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class RecipeActivity extends AppCompatActivity {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
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
            mRecipe = getIntent().getParcelableExtra(RECIPE_DATA);

            setUpExpandableStepList(setUpExpandableListData(mRecipe.getSteps()));

            setUpIngredientList(mRecipe.getIngredients());
        }
    }

}
