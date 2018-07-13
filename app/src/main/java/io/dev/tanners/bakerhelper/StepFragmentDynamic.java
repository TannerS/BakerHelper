package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class StepFragmentDynamic extends StepFragmentBase  {
    public StepFragmentDynamic() {
        // Required empty public constructor
    }

    public static StepFragmentDynamic newInstance(Step mStep) {
        StepFragmentDynamic mFragment = new StepFragmentDynamic();
        Bundle args = new Bundle();
        args.putParcelable(DYNAMIC_STEP_DATA, mStep);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mStep = getArguments().getParcelable(DYNAMIC_STEP_DATA);

        super.onActivityCreated(savedInstanceState);
    }


}
