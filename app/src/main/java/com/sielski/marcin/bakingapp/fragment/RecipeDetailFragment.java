package com.sielski.marcin.bakingapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.activity.RecipeDetailActivity;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.data.Step;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;
import com.sielski.marcin.bakingapp.util.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_step_video)
    public PlayerView mPlayerView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_step_video_frame)
    public FrameLayout mFrameLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_step_card)
    public CardView mCardView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_step_description)
    public TextView mTextView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_step_image)
    public ImageView mImageView;

    private Dialog mDialog;

    public RecipeDetailFragment() {
    }

    private int mPosition;
    private Recipe mRecipe;
    private SimpleExoPlayer mSimpleExoPlayer;
    private boolean mPlayWhenReady;
    private long mPlaybackPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPlaybackPosition = savedInstanceState.getLong(BakingAppUtils.KEY.PLAYBACK_POSITION);
            mPlayWhenReady = savedInstanceState.getBoolean(BakingAppUtils.KEY.PLAY_WHEN_READY);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BakingAppUtils.KEY.POSITION) &&
                bundle.containsKey(BakingAppUtils.KEY.RECIPE)) {
                mPosition = bundle.getInt(BakingAppUtils.KEY.POSITION);
                mRecipe = bundle.getParcelable(BakingAppUtils.KEY.RECIPE);
            }
            if (savedInstanceState == null) {
                if (bundle.containsKey(BakingAppUtils.KEY.PLAY_WHEN_READY)) {
                    mPlayWhenReady = bundle.getBoolean(BakingAppUtils.KEY.PLAY_WHEN_READY);
                }
                if (bundle.containsKey(BakingAppUtils.KEY.PLAYBACK_POSITION)) {
                    mPlaybackPosition = bundle.getLong(BakingAppUtils.KEY.PLAYBACK_POSITION);
                }
            }
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        ButterKnife.bind(this, rootView);
        Context context = getContext();
        if (mPosition == 0) {
            if (context != null) {
                mCardView.setVisibility(View.VISIBLE);
                mTextView.setText(BakingAppUtils.buildIngredients(context, mRecipe,
                        BakingAppUtils.EOL.DOUBLE, new StringBuilder()).toString());
            }
        } else {
            String url = mRecipe.steps.get(mPosition - 1).thumbnailURL;
            Uri uri = BakingAppUtils.getVideoUri(mRecipe.steps.get(mPosition - 1));
            if (context != null) {
                if (uri != null) {
                    Activity activity = getActivity();
                    if (activity != null && activity instanceof RecipeDetailActivity &&
                            ((RecipeDetailActivity) activity).getPosition() == mPosition &&
                            getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_LANDSCAPE) {
                        mDialog = new Dialog(context,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                            @Override
                            public void onBackPressed() {
                                ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
                                mFrameLayout.addView(mPlayerView);
                                mDialog.dismiss();
                                super.onBackPressed();
                            }
                        };
                        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
                        mDialog.addContentView(mPlayerView,
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                        mDialog.show();
                    }
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mPlayerView.setControllerAutoShow(false);
                    if (mSimpleExoPlayer == null) {
                        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                                new DefaultTrackSelector());
                        mPlayerView.setPlayer(mSimpleExoPlayer);
                        mSimpleExoPlayer.setPlayWhenReady(mPlayWhenReady);
                        ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(
                                new DefaultDataSourceFactory(context, Util.getUserAgent(getContext(),
                                        getString(R.string.app_name))))
                                .createMediaSource(uri);
                        mSimpleExoPlayer.prepare(extractorMediaSource);
                    } else {
                        mPlayerView.setPlayer(mSimpleExoPlayer);
                    }
                    if (mPlaybackPosition != 0) {
                        mSimpleExoPlayer.seekTo(mPlaybackPosition);
                    }
                    mSimpleExoPlayer.addListener(new Player.EventListener() {
                        @Override
                        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                        }

                        @Override
                        public void onTracksChanged(TrackGroupArray trackGroups,
                                                    TrackSelectionArray trackSelections) {

                        }

                        @Override
                        public void onLoadingChanged(boolean isLoading) {

                        }

                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            if (playbackState == Player.STATE_ENDED) {
                                mSimpleExoPlayer.seekTo(0);
                                mSimpleExoPlayer.setPlayWhenReady(false);
                            }
                        }

                        @Override
                        public void onRepeatModeChanged(int repeatMode) {

                        }

                        @Override
                        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                        }

                        @Override
                        public void onPlayerError(ExoPlaybackException error) {

                        }

                        @Override
                        public void onPositionDiscontinuity(int reason) {

                        }

                        @Override
                        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                        }

                        @Override
                        public void onSeekProcessed() {

                        }
                    });
                } else if (url != null && !url.isEmpty()) {
                    mImageView.setVisibility(View.VISIBLE);
                    GlideApp.with(context.getApplicationContext()).load(url)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder).into(mImageView);
                }
            }
            Step step = mRecipe.steps.get(mPosition - 1);
            if (!step.description.equals(step.shortDescription)) {
                mCardView.setVisibility(View.VISIBLE);
                mTextView.setText(step.description);
            }
        }
        return rootView;
    }

    @Override
    public void onStop() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onStop();
    }

    public void play() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(false);
        }
    }

    public long getPlaybackPosition() {
        if (mSimpleExoPlayer != null) {
            return mSimpleExoPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSimpleExoPlayer != null) {
            outState.putLong(BakingAppUtils.KEY.PLAYBACK_POSITION,
                    mSimpleExoPlayer.getCurrentPosition());
            outState.putBoolean(BakingAppUtils.KEY.PLAY_WHEN_READY,
                    mSimpleExoPlayer.getPlayWhenReady());
        }
    }

}
