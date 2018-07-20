package io.dev.tanners.bakerhelper.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;

public class RecipeLoader extends AsyncTaskLoader<Void> {
    private OnLoadInBackGroundCallBack mOnLoadInBackGroundCallBack;
    private Context mContext;

//    public RecipeLoader(@NonNull Context mContext, Bundle mBundle, OnLoadInBackGroundCallBack mOnLoadInBackGroundCallBack) {
    public RecipeLoader(@NonNull Context mContext, Bundle mBundle) {
        super(mContext);

        this.mContext = mContext;

//        this.mOnLoadInBackGroundCallBack = mOnLoadInBackGroundCallBack;
    }

    @Nullable
    @Override
    public Void loadInBackground() {
        if(mContext instanceof OnLoadInBackGroundCallBack)
        {
            ((OnLoadInBackGroundCallBack) mContext)._do();
        }

        return null;
    }


    public interface OnLoadInBackGroundCallBack
    {
        public void _do();
    }
}
