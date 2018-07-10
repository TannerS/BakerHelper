package io.dev.tanners.bakerhelper.recipe;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
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
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import io.dev.tanners.bakerhelper.MainActivity;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.util.ImageDisplay;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


/**
 * All samples of this code provided by https://github.com/thoughtbot/expandable-recycler-view
 */
public class StepsExpandableAdapter extends ExpandableRecyclerViewAdapter<StepsExpandableAdapter.StepHeaderViewHolder, StepsExpandableAdapter.StepBodyViewHolder> {
    private Context mContext;

    public StepsExpandableAdapter(Context mContext, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.mContext = mContext;
    }

    @Override
    public StepHeaderViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_header, parent, false);

        return new StepHeaderViewHolder(view);
    }

    @Override
    public StepBodyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_body, parent, false);

        return new StepBodyViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(StepBodyViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Step mStep = ((StepWrapper) group).getItems().get(childIndex);

        holder.mDescription.setText(mStep.getDescription());

        if (checkForProperImageType(getMimeExtType(mStep.getThumbnailUrl()))) {
            ImageDisplay.loadImage(
                    (this.mContext),
                    // glide can handle images AND videos (to be processed as a thumbnail)
                    // but for our case, we will not do that
                    mStep.getThumbnailUrl(),
                    holder.mThumbnail
            );
        } else {
            // no image, so hide it!
            holder.mThumbnail.setVisibility(View.GONE);
        }

        if (checkForProperMediaType(getMimeExtType(mStep.getVideoUrl()))) {
            Uri mUri = Uri.parse((mStep.getVideoUrl()));
            holder.initializePlayer(mUri);
        } else {
            // no video, so hide it!
            holder.mRecipeStepVideoContainer.setVisibility(View.GONE);
        }
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
    public void onBindGroupViewHolder(StepHeaderViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group);
    }

    public class StepHeaderViewHolder extends GroupViewHolder {

        private TextView mShortDescription;
        private ImageView mArrow;

        public StepHeaderViewHolder(View itemView) {
            super(itemView);
            mShortDescription = itemView.findViewById(R.id.recipe_step_short_desc);
            this.mArrow = itemView.findViewById(R.id.list_item_arrow);
        }

        public void setTitle(ExpandableGroup group) {
            mShortDescription.setText(group.getTitle());
        }

        @Override
        public void expand() {
            RotateAnimation rotate =
                    new RotateAnimation(
                            360,
                            180,
                            RELATIVE_TO_SELF,
                            0.5f,
                            RELATIVE_TO_SELF,
                            0.5f
                    );

            rotate.setDuration(200);
            rotate.setFillAfter(true);
            mArrow.setAnimation(rotate);

//            ConstraintLayout temp = itemView.findViewById(R.id.recipe_step_header);
//            temp.setBackground(itemView.getResources().getDrawable(R.drawable.recipe_step_header_after, null));
        }

        @Override
        public void collapse() {
            RotateAnimation rotate =
                    new RotateAnimation(
                            180,
                            360,
                            RELATIVE_TO_SELF,
                            0.5f,
                            RELATIVE_TO_SELF,
                            0.5f
                    );

            rotate.setDuration(200);
            rotate.setFillAfter(true);
            mArrow.setAnimation(rotate);

//            ConstraintLayout temp = itemView.findViewById(R.id.recipe_step_header);
//            temp.setBackground(itemView.getResources().getDrawable(R.drawable.recipe_step_header_before, null));
        }
    }

    public class StepBodyViewHolder extends ChildViewHolder implements View.OnClickListener, ExoPlayer.EventListener {

        private TextView mDescription;
        private SimpleExoPlayerView mPlayerView;
        private SimpleExoPlayer mExoPlayer;
        private ImageView mThumbnail;
        private FrameLayout mRecipeStepVideoContainer;

        // TODO move media logic if possible, or needed
        public StepBodyViewHolder(View itemView) {
            super(itemView);

            mDescription = itemView.findViewById(R.id.recipe_step_desc);
            mPlayerView = itemView.findViewById(R.id.recipe_step_video);
            mThumbnail = itemView.findViewById(R.id.recipe_step_thumbnail);
            mRecipeStepVideoContainer = itemView.findViewById(R.id.recipe_step_video_container);
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

                String userAgent = Util.getUserAgent(mContext, "STEPS");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        mContext, userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(false);
            }
        }


        /**
         * Release ExoPlayer.
         */
        private void releasePlayer() {
//            mNotificationManager.cancelAll();
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


}
