package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.util.ImageDisplay;

public class StepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener  {
    private Step mStep;
    private View mView;
    private FragmentStepData mCallback;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private final String mTag = StepFragment.class.getName();
    private SimpleExoPlayerView mPlayerView;
    private String userAgent;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance() {
        return new StepFragment();
    }

    private void setResources()
    {
        ImageView mThumbnail = mView.findViewById(R.id.recipe_step_thumbnail);
        TextView mDesc = mView.findViewById(R.id.recipe_step_desc);
        mPlayerView = mView.findViewById(R.id.recipe_step_video);
        FrameLayout mRecipeStepVideoContainer = mView.findViewById(R.id.recipe_step_video_container);

        if (checkForProperImageType(getMimeExtType(mStep.getThumbnailUrl()))) {
            ImageDisplay.loadImage(
                    (this.mContext),
                    // glide can handle images AND videos (to be processed as a thumbnail)
                    // but for our case, we will not do that
                    mStep.getThumbnailUrl(),
                    mThumbnail
            );
        } else {
            // no image, so hide it!
            mThumbnail.setVisibility(View.GONE);
        }

        if (checkForProperMediaType(getMimeExtType(mStep.getVideoUrl()))) {
            Uri mUri = Uri.parse((mStep.getVideoUrl()));
            initializePlayer(mUri);
        } else {
            // no video, so hide it!
            mRecipeStepVideoContainer.setVisibility(View.GONE);
        }

        mDesc.setText(mStep.getDescription());
    }

    /**
     * https://stackoverflow.com/a/8591230/2449314
     *
     * @param mUrl
     */
    private String getMimeExtType(String mUrl) {
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(mUrl);

        if (mExtension != null) {
            return MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(
                            mExtension
                    );
        }
        // if it gets here, its returning null
        // this is expected
        return mExtension;
    }

    private boolean checkForProperMediaType(String mMime) {
        if (mMime == null)
            return false;
        // can add more types later if needed
        // https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/libcore/net/MimeUtils.java
        switch (mMime) {
            case "video/mp4":
                return true;
            default:
                return false;
        }
    }

    private boolean checkForProperImageType(String mMime) {
        if (mMime == null)
            return false;
        // can add more types later if needed
        // https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/libcore/net/MimeUtils.java
        switch (mMime) {
            case "image/jpeg":
            case "image/png":
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_step, container, false);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStep = mCallback.getStep();

        setResources();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        if (context instanceof FragmentStepData) {
            mCallback = (FragmentStepData) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentStepData");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        releasePlayer();
    }

    public interface FragmentStepData {
        Step getStep();
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            Log.i("EXOPLAYER", "DEBUG 0");
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            userAgent = Util.getUserAgent(mContext, mTag);

            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(
                            mContext,
                            userAgent
                    ), new DefaultExtractorsFactory(),
                    null,
                    null
            );

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(false);
        }
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
