package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    // TODO find out if you can use this method for static and dynamic data grabbing
    public static final String DYNAMIC_STEP_DATA = "DYNAMIC_STEP_DATA";
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public StepFragmentBase() {
        // Required empty public constructor
    }

    private void setResources()
    {
        ImageView mThumbnail = mView.findViewById(R.id.recipe_step_thumbnail);
        TextView mDesc = mView.findViewById(R.id.recipe_step_desc);
        mPlayerView = mView.findViewById(R.id.recipe_step_video);
        FrameLayout mRecipeStepVideoContainer = mView.findViewById(R.id.recipe_step_video_container);

        if (!mStep.getThumbnailUrl().isEmpty() && checkForProperImageType(getMimeExtType(mStep.getThumbnailUrl()))) {
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
            setUpMediaSession();
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
        Log.i("MIME", mExtension);
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

        setResources();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    /**
     +     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     +     * PlayBackState to keep in sync.
     +     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     +     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     +     *                      STATE_BUFFERING, or STATE_ENDED.
     +     */
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

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    // this is called by external clients
    // and call onPlayerStateChanged
//    and update media session state via setPlaybackState
    private class MediaSessionCallBack extends MediaSessionCompat.Callback {
       @Override
       public void onPlay() {
           // set media session to play
           mExoPlayer.setPlayWhenReady(true);
       }

       @Override
       public void onPause() {
           // pause session
           mExoPlayer.setPlayWhenReady(false);
       }

       @Override
       public void onSkipToPrevious() {
           // restart player
           mExoPlayer.seekTo(0);
       }
   }
}
