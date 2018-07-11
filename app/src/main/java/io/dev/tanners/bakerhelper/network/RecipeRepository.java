package io.dev.tanners.bakerhelper.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeRepository {
    private Context mContext;
    private MutableLiveData<List<Recipe>> mData;

    public RecipeRepository(Context mContext) throws IOException {
        this.mContext = mContext;
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
                    // display error
                    // TODO handle later
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // TODO handle later
            }
        });
        // could be returned empty but livedata will update it when needed
        return mData;
    }
}
