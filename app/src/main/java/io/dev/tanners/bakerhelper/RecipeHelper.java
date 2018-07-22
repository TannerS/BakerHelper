package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.support.BaseBakerAdapter;
import io.dev.tanners.bakerhelper.network.NetworkCall;
import io.dev.tanners.bakerhelper.network.NetworkData;
import io.dev.tanners.bakerhelper.util.ImageDisplay;
import io.dev.tanners.bakerhelper.util.SimpleSnackBarBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class RecipeHelper extends AppCompatActivity
{
    /**
     * Run after network call
     */
    protected abstract void onPostRequest();
    protected List<Recipe> mRecipes;
    protected final static int RECIPE_LOADER = 123456789;
    protected RecipeAdapter mAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected RecyclerView mRecyclerView;

    /**
     * Get data from network
     */
    protected void getNetworkData()
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
                    Log.i("ADAPTER", "ONRESPONSE: " + mRecipes.size());
                    onPostRequest();
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

    protected void setUpAdapter(Context mContext, RecipeViewHolderHelper mCallBack)
    {
        if(mGridLayoutManager != null && mRecyclerView != null) {
            // smooth scrolling
            mGridLayoutManager.setSmoothScrollbarEnabled(true);
            // set up with layout manager
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            // create adapter
            mAdapter = new RecipeAdapter(mContext, mCallBack);
            // set adapter
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * Display message snackbar
     *
     * @param mView
     * @param mStringId
     */
    protected void displayMessage(View mView, int mStringId) {
        SimpleSnackBarBuilder.createAndDisplaySnackBar(
                mView,
                getString(mStringId),
                Snackbar.LENGTH_INDEFINITE,
                getString(R.string.loading_image_error_dismiss)
        );
    }

    protected class RecipeAdapter extends BaseBakerAdapter<Recipe>
    {
        private Context mContext;
        private RecipeViewHolderHelper mCallBack;

        public RecipeAdapter(Context mContext, RecipeViewHolderHelper mCallBack) {
            this.mContext = mContext;
            this.mCallBack = mCallBack;
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

            Recipe mRecipe = (Recipe) mBase.get(position);
            mHolder.mName.setText(mRecipe.getName());
            // for this example, all images are null
            if(mRecipe.getImage() != null || mRecipe.getImage().length() > 0) {
                ImageDisplay.loadImage(
                        (RecipeHelper.this),
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
                mCallBack.onClickHelper(mRecipe);


//
//                Intent intent = new Intent(RecipeHelper.this, RecipeActivity.class);
//
//                intent.putExtra(RecipeActivity.RECIPE_DATA, mRecipe);
//
//                startActivity(intent);
            }
        }


    }

    public interface RecipeViewHolderHelper
    {
        public void onClickHelper(Recipe mRecipe);
    }

}
