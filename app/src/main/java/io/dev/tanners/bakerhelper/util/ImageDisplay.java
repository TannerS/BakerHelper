package io.dev.tanners.bakerhelper.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * Used for helper image methods
 */
public class ImageDisplay
{
    /**
     * Load image into imageview
     *
     * @param mContext
     * @param mResource
     * @param mImageView
     */
    public static void loadImage(Context mContext, String mResource, ImageView mImageView, int mPlaceHolder) {
        // load method without placeholder, replacing it with a default placeholder
        loadImage(mContext, mResource, Color.TRANSPARENT, mImageView, mPlaceHolder);
    }

    /**
     * Load image into imageview
     *
     * @param mContext
     * @param mResource
     * @param mError
     * @param mImageView
     */
    public static void loadImage(Context mContext, String mResource, int mError, ImageView mImageView, int mPlaceHolder) {
        Glide.with(mContext)
                .load(mResource)
                .apply(new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                )
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .into(mImageView);
    }

    /**
     * Load image into imageview
     *
     * @param mContext
     * @param mResource
     * @param mError
     * @param mImageView
     */
    public static void loadImage(Context mContext, String mResource, int mError, ImageView mImageView) {
        // load method without placeholder, replacing it with a default placeholder
        loadImage(mContext,  mResource, mError, mImageView, Color.TRANSPARENT);
    }

    public static void loadImage(Context mContext, String mResource, ImageView mImageView) {
        // load method without placeholder, replacing it with a default placeholder
        loadImage(mContext,  mResource, Color.TRANSPARENT, mImageView, Color.TRANSPARENT);
    }
}
