package org.jkarsten.bakingapp.bakingapp.stepdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jkarsten.bakingapp.bakingapp.OnDualPaneInteractionListener;
import org.jkarsten.bakingapp.bakingapp.R;
import org.jkarsten.bakingapp.bakingapp.data.Food;
import org.jkarsten.bakingapp.bakingapp.data.Step;
import org.jkarsten.bakingapp.bakingapp.foodlist.FoodListActivity;
import org.jkarsten.bakingapp.bakingapp.recipedetail.RecipeDetailFragment;

import java.util.List;

public class StepDetailFragment extends Fragment {
    private OnDualPaneInteractionListener mListener;
    TextView mStepDescTextView;
    View mRootView;

    private Step[] mSteps;
    Step mStep;


    private int mStepPosition;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private Button mPreviousButton;
    private Button mNextButton;

    public StepDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        return mRootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDualPaneInteractionListener) {
            mListener = (OnDualPaneInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDualPaneInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mStepDescTextView = (TextView) mRootView.findViewById(R.id.description_textview);
        mStepPosition = getActivity().getIntent().getIntExtra(RecipeDetailFragment.STEP_ARGS, 0);
        Parcelable[] parcelables = getActivity().getIntent().getParcelableArrayExtra(FoodListActivity.FOOD_ARGS);
        mSteps = new Step[parcelables.length];
        int ii=0;
        for (Parcelable parcelable: parcelables) {
            mSteps[ii++] = (Step) parcelable;
        }

        mPreviousButton = (Button) mRootView.findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPreviousClicked(view);
            }
        });

        mNextButton = (Button) mRootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextClicked(view);
            }
        });
        setCurrentStep();
    }

    private void setCurrentStep() {
        if (mSteps != null && mStepPosition < mSteps.length) {
            mStep = mSteps[mStepPosition];
            mStepDescTextView.setText(mStep.getDescription());

            if (mExoPlayer != null) {
                releasePlayer();
            }
            intializePlayer();
            initMediaSource(mStep.getVideoURL());
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void initMediaSource(String videoURL) {
        if (mExoPlayer != null && videoURL != null && videoURL.length() > 0) {
            Uri mediaUri = Uri.parse(videoURL);
            String userAgent = Util.getUserAgent(getContext(),
                    getContext().getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void intializePlayer() {
        mPlayerView = (SimpleExoPlayerView) mRootView.findViewById(R.id.player_view);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        TrackSelector trackSelector = new DefaultTrackSelector();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        mPlayerView.setPlayer(mExoPlayer);
    }

    public void onPreviousClicked(View view) {
        if (mStepPosition - 1 >= 0) {
            mStepPosition--;
            setCurrentStep();
        }
    }

    public void onNextClicked(View view) {
        if (mStepPosition + 1 < mSteps.length) {
            mStepPosition++;
            setCurrentStep();
        }
    }
}