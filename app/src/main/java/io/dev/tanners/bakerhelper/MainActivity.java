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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.MainViewModel;
import io.dev.tanners.bakerhelper.aac.MainViewModelFactory;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.aac.RecipeRepository;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.network.GenericLoader;
import io.dev.tanners.bakerhelper.test.IdlingResourceHelper;

// TODO error checking
// TODO WAIT for network, pull db if none, else make network call
// TODO maybe display db data to load early, and update if needed
// TODO loading dialog


/**

 This class has a flow to it....

 1) make network call
 2) populate db
 3) use data from db from livedata
 4) this happens on each app start for possible data changes
 5) the db using room also does not re-insert every time the app starts, it ignores db conflicts

 */
public class MainActivity extends RecipeHelper implements LoaderManager.LoaderCallbacks<Boolean> {
    private final static String ADAPTER_RESTORE_KEY = "RECIPE_RESTORE_KEY";
    private MainViewModel mMainViewModel;
    private IdlingResourceHelper mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setState(false);
        }

        getNetworkData();
    }

    private void setUpDbData()
    {
        final RecipeDatabase mDb = RecipeDatabase.getInstance(getApplicationContext());
        mDb.getRecipeDao().insertRecipes(mRecipes);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable mSavedRecyclerLayoutState = savedInstanceState.getParcelable(ADAPTER_RESTORE_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mRecyclerView != null)
            outState.putParcelable(ADAPTER_RESTORE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void setupViewModel() {
        // load view model
        try {
            mMainViewModel = ViewModelProviders.of(
                        this,
                    new MainViewModelFactory(
                            getApplication(),
                            new RecipeRepository(this)
                    )).get(MainViewModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected void onPostRequest()
    {
        LoaderManager mLoaderManager = getSupportLoaderManager();

        Loader<Boolean> mLoader = mLoaderManager.getLoader(RECIPE_LOADER);
        // check loader instance
        if(mLoader != null)
            mLoaderManager.initLoader(RECIPE_LOADER, null, this).forceLoad();
        else
            mLoaderManager.restartLoader(RECIPE_LOADER, null, this).forceLoad();
    }

    private synchronized void getData()
    {
        mMainViewModel.getmRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> mRecipes) {
                // update adapter
                if(mAdapter != null) {
                    mAdapter.updateAdapter(mRecipes);
                    if (mIdlingResource != null) {
                        mIdlingResource.setState(true);
                    }

                }
            }
        });
    }

    private void determineAdapterProperties()
    {
        mRecyclerView = findViewById(R.id.main_recipe_list_wide);

        if(mRecyclerView != null)
        {
            mGridLayoutManager = new GridLayoutManager(this, 3);
        }
        else
        {
            mRecyclerView = findViewById(R.id.main_recipe_list);
            mGridLayoutManager = new GridLayoutManager(this, 1);
        }
    }

    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
        return new GenericLoader(this, args, new GenericLoader.OnLoadInBackGroundCallBack() {
            @Override
            public boolean _do() {
                // get data for db
                setUpDbData();
                // view model will call our db, so db needed to be populated prior
                setupViewModel();

                return true;
            }
        });
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        determineAdapterProperties();

        setUpAdapter(this, new RecipeViewHolderHelper() {
            @Override
            public void onClickHelper(Recipe mRecipe) {
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);

                intent.putExtra(RecipeActivity.RECIPE_DATA, mRecipe);

                startActivity(intent);
            }
        });

        getData();
        setUpToolbar();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new IdlingResourceHelper();
        }

        return mIdlingResource;
    }
}
