package io.dev.tanners.bakerhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.dev.tanners.bakerhelper.model.Recipe;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.FragmentData {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public Recipe getData() {
        if(getIntent() != null && getIntent().hasExtra(RECIPE_DATA))
            return getIntent().getParcelableExtra(RECIPE_DATA);

        return null;
    }
}
