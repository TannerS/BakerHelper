package io.dev.tanners.bakerhelper.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;

public class GenericLoader extends AsyncTaskLoader<Boolean> {
    protected OnLoadInBackGroundCallBack mCallback;

    /**
     * @param mContext
     * @param mBundle
     * @param mCallback
     */
    public GenericLoader(@NonNull Context mContext, Bundle mBundle, OnLoadInBackGroundCallBack mCallback) {
        super(mContext);
        this.mCallback = mCallback;
    }

    /**
     * @return
     */
    @Nullable
    @Override
    public Boolean loadInBackground() {
        // do call back
        return mCallback._do();
    }

    /**
     * call back
     */
    public interface OnLoadInBackGroundCallBack
    {
        public boolean _do();
    }
}
