package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.dev.tanners.bakerhelper.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.db.config.DBConfig;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.util.ImageDisplay;

public class MainActivity extends AppCompatActivity {
    // reference to database object
    private RecipeDatabase mDb;
    private RecipeAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // load database and data (if data is needed)
        try {
            initDb();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadResources();
        setUpRecyclerCompact();
        setupViewModel();
    }

    private void initDb() throws IOException {
        // init db
        mDb = RecipeDatabase.getInstance(getApplicationContext());
        // load db objects if needed
        loadDatabase();
    }

    private void loadDatabase() throws IOException {
        // check if data base has been created before in another app instance
        if(!getDatabasePath(DBConfig.DATABASE_NAME).exists())
        {
            Log.i("DATABASE", "DATABASE NOT CREATED ON START");
            // database does not exist, load in all data for one time only
            insertMultiple(parseJsonData());
            for(Recipe test : parseJsonData()) {
                Gson gson = new Gson();
                Log.i("RECIPE", gson.toJson(test));
            }
        }
        else
        {
            Log.i("DATABASE", "DATABASE CREATED ON START");
            // database does exist, do nothing
        }
    }

    private List<Recipe> parseJsonData() throws IOException {
        // mapper object
        ObjectMapper mapper = new ObjectMapper();
        // name of asset file to read recipe data from
        final String BAKING_DATA = "baking.json";
        // create object from file and return
        return Arrays.asList(
                mapper.readValue(
                        getAssets().open(BAKING_DATA),
                        // https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
                        Recipe[].class
                )
        );
    }

    private void insertMultiple(final List<Recipe> mRecipes) {
        // get executor to be able to run insert on separate thread
        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                // loop all recipes
                for (Recipe mRecipe : mRecipes) {
                    // insert recipe data
                    mDb.getRecipeDao().insertRecipe(mRecipe);
                }
            }
        });
    }

    private void setupViewModel()
    {
        // load view model
        MainViewModel mMainViewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        // set observer that will update adapter if changes
        mMainViewModel.getmRecipes().observe(this, new Observer<List<Recipe>>() {

            @Override
            public void onChanged(@Nullable List<Recipe> mRecipes) {
                // update adapter
                if(mAdapter != null) {
                    mAdapter.updateAdapter(mRecipes);
                }
            }
        });
    }

    private void setUpRecyclerCompact()
    {
        mGridLayoutManager = new GridLayoutManager(this, 1);
        setUpList();
    }

    private void setUpRecyclerExpanded()
    {
        mGridLayoutManager = new GridLayoutManager(this, 3);
        setUpList();
    }

    private void loadResources()
    {
        mRecyclerView = findViewById(R.id.main_recipe_list);

    }

    private void setUpList()
    {
        if(mGridLayoutManager != null) {
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

    private class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<Recipe> mRecipes;
//        private Recipe mRecipe;

        public RecipeAdapter()
        {
            mRecipes = new ArrayList<>();
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recipe_item, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RecipeViewHolder mHolder = (RecipeViewHolder) holder;


            Recipe mRecipe = mRecipes.get(position);
            mHolder.mName.setText(mRecipe.getName());

            if(mRecipe.getImage() != null || mRecipe.getImage().length() > 0) {
                ImageDisplay.loadImage(
                        ((Context) MainActivity.this),
                        // no image url in actually data
                        // just here if ever updated
                        mRecipe.getImage(),
                        R.drawable.ic_outline_error_outline_24px,
                        mHolder.mThumbnail
                );
            }
        }

        @Override
        public int getItemCount() {
            return this.mRecipes == null ? 0 : mRecipes.size();
        }

        public void updateAdapter(List<Recipe> mRecipes)
        {
            this.mRecipes = mRecipes;
            notifyDataSetChanged();
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
                Recipe mRecipe = mRecipes.get(getAdapterPosition());

                Gson gson = new Gson();
                Log.i("RECIPE!!",  gson.toJson(mRecipe));

                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                intent.putExtra(RecipeActivity.RECIPE_DATA, mRecipe);
                startActivity(intent);
            }
        }
    }

}
