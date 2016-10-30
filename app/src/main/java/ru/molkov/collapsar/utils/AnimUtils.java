package ru.molkov.collapsar.utils;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimUtils {

    public static void initShow(final View view, int delay) {
        view.setVisibility(View.INVISIBLE);
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, "translationY", dpToPx(view, 72), 0)
                .setDuration(300);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(delay);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    private static float dpToPx(View view, int dp) {
        int dpi = view.getContext().getResources().getDisplayMetrics().densityDpi;
        if (dpi == 0) {
            return 0;
        }
        return (float) (dp * (dpi / 160.0));
    }

    public static void initFabShow(final FloatingActionButton fab, final int delay) {
        fab.animate().cancel();
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.setAlpha(0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .setDuration(200)
                .setStartDelay(delay)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setInterpolator(new LinearOutSlowInInterpolator());

    }
}
