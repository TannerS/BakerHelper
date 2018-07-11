package io.dev.tanners.bakerhelper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.MainViewModel;
import io.dev.tanners.bakerhelper.aac.MainViewModelFactory;
import io.dev.tanners.bakerhelper.network.RecipeRepository;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.support.BaseBakerAdapter;
import io.dev.tanners.bakerhelper.util.ImageDisplay;

public class MainActivity extends AppCompatActivity{
    private RecipeAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewModel();
        setUpAdapter();
        getData();
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

    private void getData()
    {
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

    private void setUpAdapter()
    {
        mGridLayoutManager = new GridLayoutManager(this, 1);

        mRecyclerView = findViewById(R.id.main_recipe_list);

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
}
