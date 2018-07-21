package io.dev.tanners.bakerhelper.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.lang.ref.WeakReference;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;

public class RecipeLoader extends AsyncTaskLoader<Boolean> {
    private OnLoadInBackGroundCallBack mOnLoadInBackGroundCallBack;
    private WeakReference<Context> mContext;
    private OnLoadInBackGroundCallBack mCallback;

    public RecipeLoader(@NonNull Context mContext, Bundle mBundle, OnLoadInBackGroundCallBack mCallback) {
        super(mContext);
        this.mContext = new WeakReference<Context>(mContext);
        this.mCallback = mCallback;
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        return mCallback._do();
    }


    public interface OnLoadInBackGroundCallBack
    {
        public boolean _do();
    }
}
