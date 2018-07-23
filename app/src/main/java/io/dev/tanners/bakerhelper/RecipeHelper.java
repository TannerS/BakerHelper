package io.dev.tanners.bakerhelper;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import io.dev.tanners.bakerhelper.network.NetworkConfig;
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
        // set up network api connection with json maooer
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mMapper))
                .build();

        NetworkCall mNetworkCall = mRetrofit.create(NetworkCall.class);
        // enqueue callback on data call
        mNetworkCall.getRecipes().enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    // set object if data
                    mRecipes = response.body();
                    // run callback after connection
                    onPostRequest();
                } else {
                    // display error
                    displayMessage(findViewById(R.id.main_container), R.string.problem_with_data);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // display error
                displayMessage(findViewById(R.id.main_container), R.string.failure_to_download_data);
            }
        });
    }

    /**
     * set up adapter
     *
     * @param mCallBack
     */
    protected void setUpAdapter(RecipeViewHolderHelper mCallBack)
    {
        if(mGridLayoutManager != null && mRecyclerView != null) {
            // smooth scrolling
            mGridLayoutManager.setSmoothScrollbarEnabled(true);
            // set up with layout manager
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            // create adapter
            mAdapter = new RecipeAdapter(mCallBack);
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

    /**
     *
     */
    protected class RecipeAdapter extends BaseBakerAdapter<Recipe>
    {
        //call back
        private RecipeViewHolderHelper mCallBack;

        /**
         * @param mCallBack
         */
        public RecipeAdapter(RecipeViewHolderHelper mCallBack) {
            this.mCallBack = mCallBack;
        }

        /**
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recipe_item, parent, false);
            return new RecipeViewHolder(view);
        }

        /**
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RecipeViewHolder mHolder = (RecipeViewHolder) holder;
            // get data
            Recipe mRecipe = (Recipe) mBase.get(position);
            // set ui name
            mHolder.mName.setText(mRecipe.getName());
            // set image data
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

        /**
         *
         */
        public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mName;
            public ImageView mThumbnail;

            /**
             * @param view
             */
            public RecipeViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                // set ui references
                mName = view.findViewById(R.id.main_recipe_item_name);
                mThumbnail = view.findViewById(R.id.main_recipe_item_image);
                mName.setClipToOutline(true);
            }

            /**
             * @param v
             */
            @Override
            public void onClick(View v) {
                // get data at position
                Recipe mRecipe = (Recipe) mBase.get(getAdapterPosition());
                // call call back
                mCallBack.onClickHelper(mRecipe);
            }
        }
    }

    /**
     * call back for view holder
     */
    public interface RecipeViewHolderHelper
    {
        /**
         * @param mRecipe
         */
        public void onClickHelper(Recipe mRecipe);
    }

}
