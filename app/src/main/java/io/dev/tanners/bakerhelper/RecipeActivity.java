package io.dev.tanners.bakerhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.support.DataUtil;
import io.dev.tanners.bakerhelper.widget.config.GlobalConfig;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.FragmentData {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public Recipe getData() {
        // if data was passed in from previous activity
        if(getIntent() != null && getIntent().hasExtra(RECIPE_DATA))
            return getIntent().getParcelableExtra(RECIPE_DATA);
        return null;
    }


    /**
     * When pending intent is sent from widget
     * must load new data
     *
     * TODO possible to use dynamic fragments?
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // get fragment that is loaded from this activity
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.recipe_fragment_main);
        // Check if the fragment is an instance of the right fragment
        if (mFragment instanceof DataUtil) {
            DataUtil mDataUtil = (DataUtil) mFragment;
            // force fragment to load new data
            mDataUtil.loadNewData(intent);
        }
    }
}


