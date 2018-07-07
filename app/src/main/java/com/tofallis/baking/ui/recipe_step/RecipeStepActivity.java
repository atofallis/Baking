package com.tofallis.baking.ui.recipe_step;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tofallis.baking.R;
import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.data.StepStore;
import com.tofallis.baking.ui.RecipeIdViewModel;
import com.tofallis.baking.ui.RecipeViewModelFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;

public class RecipeStepActivity extends AppCompatActivity {

    private static final String TAG = RecipeStepActivity.class.getName();

    @BindView(R.id.videoView)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.stepDescription)
    TextView mStepDescription;

    @BindView(R.id.prevNextButtons)
    ViewGroup mButtonGroup;

    @BindView(R.id.prevButton)
    Button mPreviousStepButton;

    @BindView(R.id.nextButton)
    Button mNextStepButton;

    private int mStepPosition;
    private int mRecipeId;
    private ExoPlayer mExoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        setContentView(R.layout.recipe_step_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_STEP_POS) && intent.hasExtra(EXTRA_RECIPE_ID)) {
            // populate the UI
            mStepPosition = intent.getIntExtra(EXTRA_STEP_POS, -1);
            mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1);

            Log.d(TAG, "Pos: " + mStepPosition + " recipeId: " + mRecipeId);

            RecipeViewModelFactory factory = new RecipeViewModelFactory(AppDatabase.getDatabase(getApplicationContext()), mRecipeId);
            final RecipeIdViewModel stepsViewModel = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);

            final List<StepStore> currentStepStoreList = stepsViewModel.getStepLiveData().getValue();
            if (currentStepStoreList != null && currentStepStoreList.size() > 0) {
                updateFields(currentStepStoreList);
            }
            stepsViewModel.getStepLiveData().observe(this, this::updateFields);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public static void startStepActivity(Context context, int nextStepPos, int recipeId) {
        Intent i = new Intent(context, RecipeStepActivity.class);
        i.putExtra(EXTRA_STEP_POS, nextStepPos);
        i.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startActivity(i);
    }

    private void clickNextActivity(Context context, int nextStepPos, int recipeId) {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        }
        startStepActivity(context, nextStepPos, recipeId);
    }

    private void updateFields(List<StepStore> stepStoreList) {
        final String desc = stepStoreList.get(mStepPosition).getDescription();
        Log.d(TAG, "Desc: " + desc);
        mStepDescription.setText(desc);
        final String videoUrl = stepStoreList.get(mStepPosition).getVideoUrl();
        if (videoUrl.length() > 0) {
            setupPlayer(Uri.parse(videoUrl));
            toggleFullScreenForVideo();
        } else {
            mPlayerView.setVisibility(View.GONE);
            mStepDescription.setVisibility(View.VISIBLE);
            mButtonGroup.setVisibility(View.VISIBLE);
        }
        if (mStepPosition > 0) {
            Log.d(TAG, "Prev enabled");
            mPreviousStepButton.setEnabled(true);
            mPreviousStepButton.setOnClickListener(v -> clickNextActivity(RecipeStepActivity.this, mStepPosition-1, mRecipeId));
        } else {
            mPreviousStepButton.setEnabled(false);
        }
        if (mStepPosition < (stepStoreList.size() -1)) {
            Log.d(TAG, "Next enabled");
            mNextStepButton.setEnabled(true);
            mNextStepButton.setOnClickListener(v -> clickNextActivity(RecipeStepActivity.this, mStepPosition+1, mRecipeId));
        } else {
            mNextStepButton.setEnabled(false);
        }
    }

    private void setupPlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), new DefaultLoadControl());
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(this, "Baking");
            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(this, userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void toggleFullScreenForVideo() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                actionBar.hide();
                // also hide prev/next buttons
                mStepDescription.setVisibility(View.GONE);
                mButtonGroup.setVisibility(View.GONE);
            } else {
                actionBar.show();
            }
        }
    }
}
