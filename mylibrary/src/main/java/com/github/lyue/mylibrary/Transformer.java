/**
 * Copyright (C) 2016 yueyihui

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lyue.mylibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import java.util.Random;

/**
 * Created by c_yuelia on 16-12-12.
 */

public class Transformer {
    public interface AnimationListener {
        public void onRiseUpStart(View mTargetView, int selectedDataPosition);
        public void onRiseUpEnd(View mTargetView, int selectedDataPosition);
        public void onResetStart(View mTargetView, int selectedDataPosition);
        public void onResetEnd(View mTargetView, int selectedDataPosition);
    }
    private AnimationListener mAnimationListener;
    private Activity mActivity;
    private boolean mExtended;
    private boolean mIsAnimating;
    private float mCardOffsetDistance;
    private float mStartRadius;
    private float mEndRadius;
    private int mSelectedDataPosition;
    private View mTargetView;
    private float mMoveUp;
    private static final int LONG_DURING = 800;
    private static final int SHORT_DURING = 300;
    private static final int DELAY = LONG_DURING - SHORT_DURING;
    private static final String TAG = Transformer.class.getName();
    private static final String MAIN_FRAGMENT_TAG = "fragment_main";
    private static final String NEXT_FRAGMENT_TAG = "fragment_next";
    private static final int[] COLORs = {
            R.color.color_1, R.color.color_2, R.color.color_3,
            R.color.color_4, R.color.color_5, R.color.color_6,
            R.color.color_6, R.color.color_8, R.color.color_9,
            R.color.color_10
    };
    private int mSelectedColor;
    private View mReferenceView;
    private MaskView mTopMaskView;
    private View mMainView;
    private View mNextView;
    private android.support.v7.widget.Toolbar mV7ToolBar;
    private Rect mToolbarBounds;
    private ViewGroup.LayoutParams mToolbarLayoutParams;
    private ViewGroup mDecorView;
    private int mOldColor;

    private FragmentManager mFragmentManager;

    private Fragment mMainFragment;
    private Fragment mNextFragment;
    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public Transformer(Activity activity) {
        mActivity = activity;
        mV7ToolBar = ((GettingToolbar) activity).getV7Toolbar();
        mToolbarLayoutParams = mV7ToolBar.getLayoutParams();
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        mTopMaskView = new MaskView(mActivity);
        mDecorView.addView(mTopMaskView);

        mFragmentManager = mActivity.getFragmentManager();
        mMainFragment = mFragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
        mMainView = mMainFragment.getView();

        mNextFragment = mFragmentManager.findFragmentByTag(NEXT_FRAGMENT_TAG);
        mNextView = mNextFragment.getView();
        mNextView.setVisibility(View.INVISIBLE);
        //mFragmentManager.beginTransaction().hide(mNextFragment).commit();
    }

    public View getMainView() {
        return mMainView;
    }

    public Fragment getMainFragment() {
        return mMainFragment;
    }

    public View getNextView() {
        return mNextView;
    }

    public Fragment getNextFragment() {
        return mNextFragment;
    }

    void activateAwareMotion(View target, MainViewHolder holder,
                             int selectedDataPosition) {
        mSelectedDataPosition = selectedDataPosition;
        mTargetView = target;
        mSelectedColor = mActivity.getResources().getColor(COLORs[getRandom()]);
        mTopMaskView.setBackgroundColor(mSelectedColor);
        mReferenceView = holder.getReferenceView();
        mReferenceView.setVisibility(View.INVISIBLE);

        final Rect targetBounds = new Rect();
        final Rect topMaskViewInitBounds = new Rect();
        target.getDrawingRect(targetBounds);
        Logcat.d(TAG, "before offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mTopMaskView.getDrawingRect(topMaskViewInitBounds);
        mDecorView.offsetDescendantRectToMyCoords(target, targetBounds);

        Logcat.d(TAG, "after offsetDescendantRectToMyCoords targetBounds = " + targetBounds
                .flattenToString());
        mCardOffsetDistance = targetBounds.centerY() - topMaskViewInitBounds.centerY();
        Logcat.d(TAG, "activateAwareMotion mCardOffsetDistance = " + mCardOffsetDistance);

        mToolbarBounds = new Rect();
        mV7ToolBar.getDrawingRect(mToolbarBounds);
        mDecorView.offsetDescendantRectToMyCoords(mV7ToolBar, mToolbarBounds);

        mEndRadius = (float) Math.hypot(topMaskViewInitBounds.width() * .5f,
                topMaskViewInitBounds.height() * .5f);//√（x²+y²）
//        mStartRadius = (float) Math.hypot(mReferenceView.getWidth() * .5f,
//                mReferenceView.getHeight() * .5f);//√（x²+y²）
        mStartRadius = mReferenceView.getHeight() / 2;
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopMaskView,
                        topMaskViewInitBounds.centerX(),
                        topMaskViewInitBounds.centerY(),
                        mStartRadius,
                        mEndRadius);
        circularReveal.setDuration(LONG_DURING);

        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mCardOffsetDistance, mToolbarBounds.top);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mTopMaskView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //mFragmentManager.beginTransaction().hide(mMainFragment).commit();
                getMainView().setVisibility(View.INVISIBLE);
                riseUpRecyclerView(topMaskViewInitBounds);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                mOldColor = ((ColorDrawable) mV7ToolBar.getBackground()).getColor();
                mTopMaskView.setVisibility(View.VISIBLE);
                if (!(mTopMaskView.getTranslationY() == mCardOffsetDistance)) {
                    mTopMaskView.setTranslationY(mCardOffsetDistance);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationListener.onRiseUpEnd(mTargetView, mSelectedDataPosition);
                mV7ToolBar.setBackgroundColor(mSelectedColor);
                mTopMaskView.setVisibility(View.INVISIBLE);
                mIsAnimating = false;
                mExtended = true;
            }
        });
        if (!isAnimating()) {
            set.start();
        }
    }

    private void riseUpRecyclerView(final Rect topMaskViewInitBounds) {
        Logcat.d("TAG", "transformChildRecyclerView param topCardRect = " +
                topMaskViewInitBounds.flattenToString());
        //recyclerView was defined android:layout_height="match_parent",
        //that's mean is that it only drawing the Rect equivalent to sum of Rect of it's child view.
        //so we using height to calculate how long need to rising up.
        //NOTICE: in the end the NextRecyclerView will change height of itself,
        //so if use mMoveUp = topCardRect.bottom - mNextView.getHeight();
        //will get mMoveUp smaller and smaller
        mMoveUp = mMainView.getHeight();
        Logcat.d(TAG, "recyclerView move up = " + mMoveUp);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mMoveUp, 0);
        pathAnimator.setInterpolator(new FastOutSlowInInterpolator());
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "activateAwareMotion ValueAnimator offsetY = " + offsetY);
                mNextView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //mFragmentManager.beginTransaction().show(mNextFragment).commit();
                getNextView().setVisibility(View.VISIBLE);
                if(mAnimationListener != null) {
                    mAnimationListener.onRiseUpStart(mTargetView, mSelectedDataPosition);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pathAnimator.start();
    }

    public void reset() {
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(0, mMoveUp);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.setInterpolator(new FastOutSlowInInterpolator());
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                mNextView.setTranslationY(offsetY);
            }
        });
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
                resetTopMaskView();
                mAnimationListener.onResetStart(mTargetView, mSelectedDataPosition);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
               /* mFragmentManager.beginTransaction().hide(mNextFragment).commit();
                mFragmentManager.beginTransaction().show(mMainFragment).commit();*/
                getNextView().setVisibility(View.INVISIBLE);
                getMainView().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (!isAnimating()) {
            pathAnimator.start();
        }
    }

    private void resetTopMaskView() {
        Rect topMaskViewBounds = new Rect();
        mTopMaskView.getDrawingRect(topMaskViewBounds);

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mTopMaskView,
                        topMaskViewBounds.centerX(),
                        topMaskViewBounds.centerY(),
                        mEndRadius,
                        mStartRadius);
        circularReveal.setDuration(LONG_DURING);

        Logcat.d(TAG, "reset mCardOffsetDistance = " + mCardOffsetDistance);
        ValueAnimator pathAnimator = ValueAnimator.
                ofFloat(mToolbarBounds.top, mCardOffsetDistance);
        pathAnimator.setDuration(SHORT_DURING);
        pathAnimator.setStartDelay(DELAY);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offsetY = (float) animation.getAnimatedValue();
                Logcat.d(TAG, "raiseUpRecyclerView ValueAnimator offsetY = " + offsetY);
                mTopMaskView.setTranslationY(offsetY);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTopMaskView.setVisibility(View.VISIBLE);
                mV7ToolBar.setBackgroundColor(mOldColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationListener.onResetEnd(mTargetView, mSelectedDataPosition);
                mTopMaskView.setVisibility(View.INVISIBLE);
                mReferenceView.setVisibility(View.VISIBLE);
                mIsAnimating = false;
                mExtended = false;
            }
        });
        set.start();
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public boolean isExtended() {
        return mExtended;
    }

    public int getSelectedChildPosition() {
        return mSelectedDataPosition;
    }

    private int getRandom() {
        Random random=new Random();
        return random.nextInt(10);
    }

    private class MaskView extends View {

        public MaskView(Context context) {
            super(context);
            setLayoutParams(mToolbarLayoutParams);
            setVisibility(View.INVISIBLE);
        }
    }
}
