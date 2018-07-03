package com.tofallis.baking.ui.recipe_step;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

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
    VideoView mVideo;

    @BindView(R.id.stepDescription)
    TextView mStepDescription;

    @BindView(R.id.prevButton)
    Button mPreviousStepButton;

    @BindView(R.id.nextButton)
    Button mNextStepButton;

    private int mStepPosition;
    private int mRecipeId;

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

    private void updateFields(List<StepStore> stepStoreList) {
        final String desc = stepStoreList.get(mStepPosition).getDescription();
        Log.d(TAG, "Desc: " + desc);
        mStepDescription.setText(desc);
        mVideo.setVideoURI(Uri.parse(stepStoreList.get(mStepPosition).getVideoUrl()));
        if (mStepPosition > 0) {
            Log.d(TAG, "Prev enabled");
            mPreviousStepButton.setEnabled(true);
            mPreviousStepButton.setOnClickListener(v -> startStepActivity(RecipeStepActivity.this, mStepPosition-1, mRecipeId));
        } else {
            mPreviousStepButton.setEnabled(false);
        }
        if (mStepPosition < (stepStoreList.size() -1)) {
            Log.d(TAG, "Next enabled");
            mNextStepButton.setEnabled(true);
            mNextStepButton.setOnClickListener(v -> startStepActivity(RecipeStepActivity.this, mStepPosition+1, mRecipeId));
        } else {
            mNextStepButton.setEnabled(false);
        }
    }

    public static void startStepActivity(Context context, int nextStepPos, int recipeId) {
        Intent i = new Intent(context, RecipeStepActivity.class);
        i.putExtra(EXTRA_STEP_POS, nextStepPos);
        i.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startActivity(i);
    }
}
