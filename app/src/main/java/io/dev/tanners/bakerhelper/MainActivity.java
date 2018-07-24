package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.MainViewModel;
import io.dev.tanners.bakerhelper.aac.MainViewModelFactory;
import io.dev.tanners.bakerhelper.aac.RecipeRepository;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.test.IdlingResourceHelper;

/**
 This class has a flow to it....

 1) make network call
 2) populate db
 3) use data from db from livedata
 4) this happens on each app start for possible data changes
 5) the db using room also does not re-insert every time the app starts, it ignores db conflicts

 */
public class MainActivity extends RecipeBase {
    private final static String ADAPTER_RESTORE_KEY = "RECIPE_RESTORE_KEY";
    private MainViewModel mMainViewModel;
    private IdlingResourceHelper mIdlingResource;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // used for testing to wait for network call to finish
        getIdlingResource();
        // set state of idling resource
        if (mIdlingResource != null) {
            mIdlingResource.setState(false);
        }
        // view model will call our db, so db needed to be populated prior
        setupViewModel();
        // determine layout
        determineAdapterProperties();
        // set up adapter
        setUpAdapter();
        // set up toolbar
        setUpToolbar();
        // get data for adapter
        try {
            getData();
        } catch (IOException e) {
            displayMessage(findViewById(R.id.main_container), R.string.problem_with_data);
            e.printStackTrace();
        }
    }

    /**
     * restore list positions
     *
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get list positions
        if(savedInstanceState != null) {
            if(mRecyclerView != null) {
                // get parcelable by key
                Parcelable mSavedRecyclerLayoutState = savedInstanceState.getParcelable(ADAPTER_RESTORE_KEY);
                // restore pos
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
            }
        }
    }

    /**
     * restore list position
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save list pos
        if(mRecyclerView != null)
            outState.putParcelable(ADAPTER_RESTORE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    /**
     * set activities view model
     */
    private void setupViewModel() {
        // load view model
        // get instance of view model
        mMainViewModel = ViewModelProviders.of(
                    this,
                new MainViewModelFactory(
                        getApplication(),
                        RecipeRepository.getInstance(this)
                )).get(MainViewModel.class);
    }

    /**
     * get recipe data from view model via live data
     */
    private synchronized void getData() throws IOException {
        // get data from view model via live data's observer
        mMainViewModel.getmRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> mRecipes) {
                if(mAdapter != null) {
                    // update adapter
                    mAdapter.updateAdapter(mRecipes);
                    // set idling resource to true for testing
                    // to mark end of async task
                    if (mIdlingResource != null) {
                        mIdlingResource.setState(true);
                    }

                }
            }
        });
    }

    /**
     * depending on the elements in the UI, determines wat to load
     * this differs based on screen size
     */
    private void determineAdapterProperties()
    {
        // check for this recyclerview
        mRecyclerView = findViewById(R.id.main_recipe_list_wide);
        // if not null, we have tablet
        if(mRecyclerView != null)
        {
            // so load 3 columns
            mGridLayoutManager = new GridLayoutManager(this, 3);
        }
        // non-tablet
        else
        {
            // it is not a tablet, so load the proper ui elements
            mRecyclerView = findViewById(R.id.main_recipe_list);
            // and set for single element column
            mGridLayoutManager = new GridLayoutManager(this, 1);
        }
    }

    /**
     * set up toolbar for activity
     */
    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        // set toolbar text to
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
    }

    /**
     * set up the adapter with the layout
     */
    private void setUpAdapter()
    {
        // set up recipe adapter with callback
        setUpAdapter(new RecipeViewHolderHelper() {
            @Override
            public void onClickHelper(Recipe mRecipe) {
                // create intent to load new activity
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                // insert recipe object into extra
                intent.putExtra(RecipeActivity.RECIPE_DATA, mRecipe);
                // start activity
                startActivity(intent);
            }
        });
    }

    /**
     * get idling resource for this activity for test class
     *
     * @return
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new IdlingResourceHelper();
        }
        // return it
        return mIdlingResource;
    }
}
