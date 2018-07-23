package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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

public abstract class StepFragmentBase extends Fragment implements View.OnClickListener, ExoPlayer.EventListener  {
    protected Step mStep;
    protected View mView;
    protected Context mContext;
    protected SimpleExoPlayer mExoPlayer;
    protected final String mTag = StepFragmentBase.class.getName();
    protected SimpleExoPlayerView mPlayerView;
    protected String userAgent;
    public static final String DYNAMIC_STEP_DATA = "DYNAMIC_STEP_DATA";
    protected MediaSessionCompat mMediaSession;
    protected PlaybackStateCompat.Builder mStateBuilder;

    /**
     *
     */
    public StepFragmentBase() {
        // Required empty public constructor
    }

    /**
     * set up ui resources
     */
    private void setResources()
    {
        ImageView mThumbnail = mView.findViewById(R.id.recipe_step_thumbnail);
        TextView mDesc = mView.findViewById(R.id.recipe_step_desc);
        mPlayerView = mView.findViewById(R.id.recipe_step_video);
        // set up image
        if (!mStep.getThumbnailUrl().isEmpty() && checkForProperImageType(getMimeExtType(mStep.getThumbnailUrl()))) {
            ImageDisplay.loadImage(
                    (this.mContext),
                    // glide can handle images AND videos (to be processed as a thumbnail)
                    // but for our case, we will not do that
                    mStep.getThumbnailUrl(),
                    mThumbnail
            );
        // no image hid it
        } else {
            // no image, so hide it!
            mThumbnail.setVisibility(View.GONE);
        }
        // check for video
        if (checkForProperMediaType(getMimeExtType(mStep.getVideoUrl()))) {
            Uri mUri = Uri.parse((mStep.getVideoUrl()));
            setUpMediaSession();
            initializePlayer(mUri);
        // is none, hide it
        } else {
            ConstraintLayout mRecipeStepVideoContainer = mView.findViewById(R.id.fragment_step_partial_header);
            if(mRecipeStepVideoContainer != null)
                // no video, so hide it!
                mRecipeStepVideoContainer.setVisibility(View.GONE);
        }
        // set description
        mDesc.setText(mStep.getDescription());
    }

    /**
     * https://stackoverflow.com/a/8591230/2449314
     *
     * @param mUrl
     */
    private String getMimeExtType(String mUrl) {
        // check mime type
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(mUrl);
        // if ext exist, return type
        if (mExtension != null) {
            return MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(
                            mExtension
                    );
        }
        // if it gets here, its returning null
        return mExtension;
    }

    /**
     * check for media type (audio)
     *
     * @param mMime
     * @return
     */
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

    /**
     * check media type (image)
     *
     * @param mMime
     * @return
     */
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

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_step, container, false);
        // return view
        return mView;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set ui resources
        setResources();
    }

    /**
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // set context
        mContext = context;
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // release audio player
        releasePlayer();
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            // get instance
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            // set ui
            mPlayerView.setPlayer(mExoPlayer);
            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);
            // get user agent
            userAgent = Util.getUserAgent(mContext, mTag);
            // get media source
            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(
                            mContext,
                            userAgent
                    ), new DefaultExtractorsFactory(),
                    null,
                    null
            );
            // set up exo player
            mExoPlayer.prepare(mediaSource);
            // disable auto play
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * set media session so external controls can control audio player
     */
    private void setUpMediaSession()
    {
        mMediaSession = new MediaSessionCompat(mContext, mTag);
        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );
        // when app is not visible, don't let media buttons restart the player
        mMediaSession.setMediaButtonReceiver(null);
        // set init state so media buttons can control the player
        mStateBuilder = new PlaybackStateCompat.Builder()
        .setActions(
                PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
        );
        // build state and set
        mMediaSession.setPlaybackState(mStateBuilder.build());
        //  handle callbacks from a media controller.
        mMediaSession.setCallback(new MediaSessionCallBack());
        // Start the Media Session
        mMediaSession.setActive(true);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(mExoPlayer != null)
        {
            // stop and release player
            mExoPlayer.stop();
            mExoPlayer.release();
        }
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        // overridden in child classes
    }

    /**
     * @param timeline
     * @param manifest
     */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    /**
     * @param trackGroups
     * @param trackSelections
     */
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    /**
     * @param isLoading
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // when state is ready and playWhenReady is true meaning exoplayer is playing
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        // state is ready and player is paused
        } else if ((playbackState == ExoPlayer.STATE_READY) && !playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        // set state
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    /**
     * @param error
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    /**
     *
     */
    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * this is called by external clients
     * and call onPlayerStateChanged
     *  and update media session state via setPlaybackState
     */
    private class MediaSessionCallBack extends MediaSessionCompat.Callback {
        /**
         *
         */
       @Override
       public void onPlay() {
           // set media session to play
           mExoPlayer.setPlayWhenReady(true);
       }

        /**
         *
         */
       @Override
       public void onPause() {
           // pause session
           mExoPlayer.setPlayWhenReady(false);
       }

        /**
         *
         */
       @Override
       public void onSkipToPrevious() {
           // restart player
           mExoPlayer.seekTo(0);
       }
   }
}
