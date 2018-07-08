package com.tofallis.baking.ui.recipe_step;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_STEP_ACTIVITY;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;

public class RecipeStepFragment extends Fragment {

    private static final String TAG = RecipeStepFragment.class.getName();

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

    private boolean mPhone;

    @VisibleForTesting // flag any direct production usage as unexpected
    public RecipeStepFragment() {
    }

    public static RecipeStepFragment newTabletInstance(int recipeId, int stepPosition) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(setupBaseFragmentBundle(recipeId, stepPosition));
        return fragment;
    }

    public static RecipeStepFragment newPhoneInstance(int recipeId, int stepPosition) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        Bundle args = setupBaseFragmentBundle(recipeId, stepPosition);
        args.putBoolean(EXTRA_RECIPE_STEP_ACTIVITY, true);
        fragment.setArguments(args);
        return fragment;
    }

    private static Bundle setupBaseFragmentBundle(int recipeId, int stepPosition) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_RECIPE_ID, recipeId);
        args.putInt(EXTRA_STEP_POS, stepPosition);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null) {
            throw new InstantiationError();
        }
        mRecipeId = Objects.requireNonNull(args.getInt(EXTRA_RECIPE_ID));
        mStepPosition = Objects.requireNonNull(args.getInt(EXTRA_STEP_POS));
        mPhone = args.getBoolean(EXTRA_RECIPE_STEP_ACTIVITY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_fragment, container, false);
        ButterKnife.bind(this, rootView);

        // populate the UI
        Log.d(TAG, "Pos: " + mStepPosition + " recipeId: " + mRecipeId);

        if (mPhone) {
            mButtonGroup.setVisibility(View.VISIBLE);
        }

        RecipeViewModelFactory factory = new RecipeViewModelFactory(AppDatabase.getDatabase(getContext().getApplicationContext()), mRecipeId);
        final RecipeIdViewModel stepsViewModel = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);

        final List<StepStore> currentStepStoreList = stepsViewModel.getStepLiveData().getValue();
        if (currentStepStoreList != null && currentStepStoreList.size() > 0) {
            updateFields(currentStepStoreList);
        }
        stepsViewModel.getStepLiveData().observe(this, this::updateFields);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        }
    }

    public static void clickNextActivity(Context context, int recipeId, int nextStepPos) {
        RecipeStepActivity.startStepActivity(context, recipeId, nextStepPos);
    }

    private void updateFields(List<StepStore> stepStoreList) {
        if (stepStoreList == null || stepStoreList.size() == 0) {
            return;
        }
        final String desc = stepStoreList.get(mStepPosition).getDescription();
        Log.d(TAG, "Desc: " + desc);
        mStepDescription.setText(desc);
        final String videoUrl = stepStoreList.get(mStepPosition).getVideoUrl();
        if (videoUrl.length() > 0) {
            setupPlayer(Uri.parse(videoUrl));
            toggleFullsizeVideo();
        } else {
            mPlayerView.setVisibility(View.GONE);
        }
        if (mStepPosition > 0) {
            Log.d(TAG, "Prev enabled");
            mPreviousStepButton.setEnabled(true);
            mPreviousStepButton.setOnClickListener(v -> clickNextActivity(getContext(), mRecipeId, mStepPosition - 1));
        } else {
            mPreviousStepButton.setEnabled(false);
        }
        if (mStepPosition < (stepStoreList.size() - 1)) {
            Log.d(TAG, "Next enabled");
            mNextStepButton.setEnabled(true);
            mNextStepButton.setOnClickListener(v -> clickNextActivity(getContext(), mRecipeId, mStepPosition + 1));
        } else {
            mNextStepButton.setEnabled(false);
        }
    }

    private void setupPlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(), new DefaultLoadControl());
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getContext(), "Baking");
            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
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

    private void toggleFullsizeVideo() {
        if (mPhone) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mStepDescription.setVisibility(View.GONE);
                mButtonGroup.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                params.width = MATCH_PARENT;
                params.height = MATCH_PARENT;
                mPlayerView.setLayoutParams(params);
            } else {
                mStepDescription.setVisibility(View.VISIBLE);
                mButtonGroup.setVisibility(View.VISIBLE);
            }
        }
    }
}
