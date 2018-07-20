package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.MainViewModel;
import io.dev.tanners.bakerhelper.aac.MainViewModelFactory;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.network.NetworkCall;
import io.dev.tanners.bakerhelper.network.NetworkData;
import io.dev.tanners.bakerhelper.aac.RecipeRepository;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.support.BaseBakerAdapter;
import io.dev.tanners.bakerhelper.network.RecipeLoader;
import io.dev.tanners.bakerhelper.test.IdlingResourceHelper;
import io.dev.tanners.bakerhelper.util.ImageDisplay;
import io.dev.tanners.bakerhelper.util.SimpleSnackBarBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// TODO error checking
// TODO WAIT for network, pull db if none, else make network call
// TODO maybe display db data to load early, and update if needed
// TODO loading dialog
public class MainActivity extends AppCompatActivity implements RecipeLoader.OnLoadInBackGroundCallBack, LoaderManager.LoaderCallbacks<Void> {
    private final static String ADAPTER_RESTORE_KEY = "RECIPE_RESTORE_KEY";
    private final static int RECIPE_LOADER = 123456789;
    private RecipeAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private MainViewModel mMainViewModel;
    private IdlingResourceHelper mIdlingResource;
    private List<Recipe> mRecipes;

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

    // todo https://stackoverflow.com/questions/10695152/java-pattern-for-nested-callbacks
//    private void setUpDbData(final List<Recipe> mRecipes)
//    {
//        // TODO dagger 2?
//        final RecipeDatabase mDb = RecipeDatabase.getInstance(getApplicationContext());
//
//        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                mDb.getRecipeDao().insertRecipes(mRecipes);
//                finishLoadingResources();
//            }
//        });
//    }

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

    private void getNetworkData()
    {
        ObjectMapper mMapper = new ObjectMapper();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkData.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mMapper))
                .build();

        NetworkCall mNetworkCall = mRetrofit.create(NetworkCall.class);

        mNetworkCall.getRecipes().enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    mRecipes = response.body();
                    loadLoader();
                } else {
                    displayMessage(findViewById(R.id.main_container), R.string.problem_with_data);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                displayMessage(findViewById(R.id.main_container), R.string.failure_to_download_data);
            }
        });
    }

    private void loadLoader()
    {
        LoaderManager mLoaderManager = getSupportLoaderManager();

        Loader<Void> mLoader = mLoaderManager.getLoader(RECIPE_LOADER);
        // check loader instance
        if(mLoader != null)
            mLoaderManager.initLoader(RECIPE_LOADER, null, this).forceLoad();
        else
            mLoaderManager.restartLoader(RECIPE_LOADER, null, this).forceLoad();
    }

    private void displayMessage(View mView, int mStringId) {
        SimpleSnackBarBuilder.createAndDisplaySnackBar(
                mView,
                getString(mStringId),
                Snackbar.LENGTH_INDEFINITE,
                getString(R.string.loading_image_error_dismiss)
        );
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

    private void setUpAdapter()
    {
        if(mGridLayoutManager != null && mRecyclerView != null) {
            // smooth scrolling
            mGridLayoutManager.setSmoothScrollbarEnabled(true);
            // set up with layout manager
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            // create adapter
            mAdapter = new RecipeAdapter();
            // set adapter
            mRecyclerView.setAdapter(mAdapter);
        }
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

    @Override
    public void _do() {
        // get data for db
        setUpDbData();
        // view model will call our db, so db needed to be populated prior
        setupViewModel();
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {
        return new RecipeLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
        determineAdapterProperties();
        setUpAdapter();
        getData();
        setUpToolbar();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {

    }

    protected class RecipeAdapter extends BaseBakerAdapter<Recipe>
    {
        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recipe_item, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RecipeViewHolder mHolder = (RecipeViewHolder) holder;

            Recipe mRecipe = (Recipe) mBase.get(position);
            mHolder.mName.setText(mRecipe.getName());
            // for this example, all images are null
            if(mRecipe.getImage() != null || mRecipe.getImage().length() > 0) {
                ImageDisplay.loadImage(
                        (MainActivity.this),
                        // no image url in actually data
                        // just here if ever updated
                        mRecipe.getImage(),
                        R.drawable.ic_outline_error_outline_24px,
                        mHolder.mThumbnail
                );
            }
        }

        public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mName;
            public ImageView mThumbnail;

            public RecipeViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                mName = view.findViewById(R.id.main_recipe_item_name);
                mThumbnail = view.findViewById(R.id.main_recipe_item_image);
                mName.setClipToOutline(true);
            }

            @Override
            public void onClick(View v) {
                Recipe mRecipe = (Recipe) mBase.get(getAdapterPosition());

                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);

                intent.putExtra(RecipeActivity.RECIPE_DATA, mRecipe);

                startActivity(intent);
            }
        }
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
