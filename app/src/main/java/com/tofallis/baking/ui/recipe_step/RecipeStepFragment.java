package com.tofallis.baking.ui.recipe_step;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
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
import com.squareup.picasso.Picasso;
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
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_STEP_ACTIVITY;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_VIDEO_KEY_POSITION;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_VIDEO_KEY_WINDOW;

public class RecipeStepFragment extends Fragment {

    private static final String TAG = RecipeStepFragment.class.getName();

    @BindView(R.id.videoView)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.stepDetails)
    ViewGroup mStepDetails;

    @BindView(R.id.stepDescription)
    TextView mStepDescription;

    @BindView(R.id.stepThumbnail)
    ImageView mStepThumbnail;

    @BindView(R.id.prevNextButtons)
    ViewGroup mButtonGroup;

    @BindView(R.id.prevButton)
    Button mPreviousStepButton;

    @BindView(R.id.nextButton)
    Button mNextStepButton;

    private int mStepPosition;
    private int mRecipeId;
    private String mRecipeName;

    private ExoPlayer mExoPlayer;

    private int mStartWindow;
    private long mStartPosition;

    private boolean mPhone;
    private RecipeIdViewModel mStepsViewModel;

    @VisibleForTesting // flag any direct production usage as unexpected
    public RecipeStepFragment() {
    }

    public static RecipeStepFragment newTabletInstance(int recipeId, String recipeName, int stepPosition) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(setupBaseFragmentBundle(recipeId, recipeName, stepPosition));
        return fragment;
    }

    public static RecipeStepFragment newPhoneInstance(int recipeId, String recipeName, int stepPosition) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        Bundle args = setupBaseFragmentBundle(recipeId, recipeName, stepPosition);
        args.putBoolean(EXTRA_RECIPE_STEP_ACTIVITY, true);
        fragment.setArguments(args);
        return fragment;
    }

    private static Bundle setupBaseFragmentBundle(int recipeId, String recipeName, int stepPosition) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_RECIPE_ID, recipeId);
        args.putInt(EXTRA_STEP_POS, stepPosition);
        args.putString(EXTRA_RECIPE_NAME, recipeName);
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
        mRecipeName = Objects.requireNonNull(args.getString(EXTRA_RECIPE_NAME));
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
        mStepsViewModel = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);
        mStepsViewModel.getStepLiveData().observe(this, this::updateFields);
        return rootView;
    }

    private void updateViewsIfNeeded() {
        final List<StepStore> currentStepStoreList = mStepsViewModel.getStepLiveData().getValue();
        if (currentStepStoreList != null && currentStepStoreList.size() > 0) {
            updateFields(currentStepStoreList);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewsIfNeeded();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        updatePlaybackPosition();
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(EXTRA_STEP_VIDEO_KEY_WINDOW, mStartWindow);
        outState.putLong(EXTRA_STEP_VIDEO_KEY_POSITION, mStartPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mStartWindow = savedInstanceState.getInt(EXTRA_STEP_VIDEO_KEY_WINDOW);
            mStartPosition = savedInstanceState.getLong(EXTRA_STEP_VIDEO_KEY_POSITION);
            Log.d(TAG, "onActivityCreated :: mStartPos:" + mStartPosition + " mStartWindow: " + mStartWindow);
        } else {
            Log.d(TAG, "onActivityCreated :: saved state is null!");
            clearStartPosition();
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
        releasePlayer();
    }

    public void clickNextActivity(int nextStepPos) {
        RecipeStepActivity.startStepActivity(getContext(), mRecipeId, mRecipeName, nextStepPos);
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
        final String thumbnailUrl = stepStoreList.get(mStepPosition).getThumbnailUrl();
        final @DrawableRes int placeholder = R.drawable.chef;
        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            mStepThumbnail.setImageResource(placeholder);
        } else {
            Picasso.with(getContext())
                    .load(thumbnailUrl)
                    .placeholder(placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .into(mStepThumbnail);
        }

        if (mStepPosition > 0) {
            Log.d(TAG, "Prev enabled");
            mPreviousStepButton.setEnabled(true);
            mPreviousStepButton.setOnClickListener(v -> clickNextActivity(mStepPosition - 1));
        } else {
            mPreviousStepButton.setEnabled(false);
        }
        if (mStepPosition < (stepStoreList.size() - 1)) {
            Log.d(TAG, "Next enabled");
            mNextStepButton.setEnabled(true);
            mNextStepButton.setOnClickListener(v -> clickNextActivity(mStepPosition + 1));
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

            final boolean haveStartPosition = mStartWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                mExoPlayer.seekTo(mStartWindow, mStartPosition);
            }
            Log.d(TAG, "haveStartPos: " + haveStartPosition);
            mExoPlayer.prepare(mediaSource, !haveStartPosition, false);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void updatePlaybackPosition() {
        if (mExoPlayer != null) {
            mStartWindow = mExoPlayer.getCurrentWindowIndex();
            mStartPosition = Math.max(0, mExoPlayer.getContentPosition());
        }
    }

    private void clearStartPosition() {
        mStartWindow = C.INDEX_UNSET;
        mStartPosition = C.TIME_UNSET;
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            updatePlaybackPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void toggleFullsizeVideo() {
        if (mPhone) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mStepDetails.setVisibility(View.GONE);
                mButtonGroup.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                params.width = MATCH_PARENT;
                params.height = MATCH_PARENT;
                mPlayerView.setLayoutParams(params);
            } else {
                mStepDetails.setVisibility(View.VISIBLE);
                mButtonGroup.setVisibility(View.VISIBLE);
            }
        }
    }
}
