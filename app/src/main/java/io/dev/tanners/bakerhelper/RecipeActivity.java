package io.dev.tanners.bakerhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.support.DataUtil;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.FragmentData {
    // used for key for data bundle passed through intent
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    /**
     * call back for fragment, this means fragment can grab data from activity
     * in this case, it is the extra passed into the activity
     *
     * @return
     */
    @Override
    public Recipe getData() {
        // if data was passed in from previous activity
        if(getIntent() != null && getIntent().hasExtra(RECIPE_DATA))
            // return data
            return getIntent().getParcelableExtra(RECIPE_DATA);
        // if data is not passed in via intent, this will return null
        // this is used for something else if its null
        return null;
    }

    /**
     * When pending intent is sent from widget
     * must load new data
     * this is using a callback to alert the fragment
     * that holds this activity to alert t of new data
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


