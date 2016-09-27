package com.example.emanuel.myapplication.slack;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by emanuel on 26/9/16.
 */
public class EventItemAnimator extends DefaultItemAnimator {

    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();

    public EventItemAnimator() {
        setAddDuration(1000);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        ViewCompat.setAlpha(holder.itemView, 0);
        ViewCompat.setTranslationX(holder.itemView, holder.itemView.getWidth());
        mPendingAdditions.add(holder);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        super.runPendingAnimations();
        boolean additionsPending = !mPendingAdditions.isEmpty();
        if (additionsPending) {
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(mPendingAdditions);
            mAdditionsList.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                public void run() {
                    for (RecyclerView.ViewHolder holder : additions) {
                        animateAddImpl(holder);
                    }
                    additions.clear();
                    mAdditionsList.remove(additions);
                }
            };
            adder.run();
        }
    }

    private void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mAddAnimations.add(holder);
        animation.alpha(1)
                .translationX(0)
                .setDuration(getAddDuration())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        dispatchAddStarting(holder);
                    }
                    @Override
                    public void onAnimationCancel(View view) {
                        ViewCompat.setAlpha(view, 1);
                        ViewCompat.setTranslationX(view, 0);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        animation.setListener(null);
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
    }

    @Override
    public boolean isRunning() {
        return !mPendingAdditions.isEmpty() || super.isRunning();
    }

    /**
     * Check the state of currently pending and running animations. If there are none
     * pending/running, call {@link #dispatchAnimationsFinished()} to notify any
     * listeners.
     */
    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }
}
