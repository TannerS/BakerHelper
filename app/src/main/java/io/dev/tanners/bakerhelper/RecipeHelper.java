package io.dev.tanners.bakerhelper;

import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.network.NetworkCall;
import io.dev.tanners.bakerhelper.network.NetworkData;
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
}
