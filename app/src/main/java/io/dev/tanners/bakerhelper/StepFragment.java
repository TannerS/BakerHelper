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

// TODO fix text size on this page in tablet mode
// TODO put fake photo to test image size and style (just in case)

/**
 * DISCLAIMER *******************************
 *
 * This class is used as a static fragment  and dynamic
 * if this is a static fragment for phone mode, data is passed in via a call back
 * if it is a dynamic fragment, the data is passed in as a bundle
 * this is due to the design. the static fragment is set via xml and has no data,
 * so that is why we use the call back, while the dynamic one has no data, BUT since
 * the fragment is being called from another fragment, I thought it was bad practice to
 * use another callback to talk to a fragment when it is said fragments should never talk
 * directly to each other, and using the activity (at the time I am writing this) is nore problems
 * then it is worth, when a simple bundle will work. this only changes a few checks in the
 * onActivityCreated to determine what data to use (one will be null)
 */
public class StepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener  {
    private Step mStep;
    private View mView;
    // this is used for static fragment implementation
    // other methods are used for dynamic
    private FragmentStepData mCallback;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private final String mTag = StepFragment.class.getName();
    private SimpleExoPlayerView mPlayerView;
    private String userAgent;
    // TODO find out if you can use this method for static and dynamic data grabbing
    public static final String DYNAMIC_STEP_DATA = "DYNAMIC_STEP_DATA";

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(Step mStep) {
//    public static StepFragment newInstance() {
        StepFragment mFragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(DYNAMIC_STEP_DATA, mStep);
        mFragment.setArguments(args);

        return mFragment;
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

        // if not null, its a static fragment
        if(mCallback != null)
            mStep = mCallback.getStep();
        // dynamic fragment
        else
            mStep = getArguments().getParcelable(DYNAMIC_STEP_DATA);

        setResources();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        // TODO maybe catch for dynamic and set as null

        // static fragment if this is created from the activity
        if (context instanceof FragmentStepData) {
            // static fragment
            mCallback = (FragmentStepData) context;
        // dynamic fragment if called from a activity not needed this interface
        } else {
            mCallback = null;

//            throw new RuntimeException(context.toString()
//                    + " must implement FragmentStepData");
        }
//
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
        if(mExoPlayer != null)
        {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
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
