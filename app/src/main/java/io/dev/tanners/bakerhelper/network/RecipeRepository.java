package io.dev.tanners.bakerhelper.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.util.SimpleSnackBarBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeRepository {
    private Context mContext;
    private View mView;
    private MutableLiveData<List<Recipe>> mData;

    public RecipeRepository(Context mContext, View mView) throws IOException {
        this.mContext = mContext;
        this.mView = mView;
    }

    public LiveData<List<Recipe>> getAllRecipes() throws IOException {
        mData = new MutableLiveData<List<Recipe>>();

        ObjectMapper mMapper = new ObjectMapper();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(
                        NetworkData.BASE_URL
                )
                .addConverterFactory(JacksonConverterFactory.create(mMapper))
                .build();

        NetworkCall mNetworkCall = mRetrofit.create(NetworkCall.class);

        mNetworkCall.getRecipes().enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    // call callback after getting data
                    // ********This is async, so data will be returned empty before anything happens
                    // however, this live data is observing changes so that is why it still works
                    mData.setValue(response.body());
                } else {
                    displayMessage(R.string.problem_with_data);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                displayMessage(R.string.failure_to_download_data);
            }
        });
        // could be returned empty but livedata will update it when needed
        return mData;
    }

    private void displayMessage(int mStringId) {
        SimpleSnackBarBuilder.createAndDisplaySnackBar(mView,
                mContext.getString(mStringId),
                Snackbar.LENGTH_INDEFINITE,
                mContext.getString(R.string.loading_image_error_dismiss));
    }
}
