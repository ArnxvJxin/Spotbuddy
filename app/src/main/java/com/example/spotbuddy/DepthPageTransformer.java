package com.example.spotbuddy;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) {
            // This page is off-screen to the left
            view.setAlpha(0f);
        } else if (position <= 0) {
            // This page is on-screen and moving from left to right
            view.setAlpha(1 + position);
            view.setTranslationX(pageWidth * position);
            view.setTranslationY(0f);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else if (position <= 1) {
            // This page is on-screen and moving from right to left
            view.setAlpha(1 - position);
            view.setTranslationX(-pageWidth * position);
            view.setTranslationY(0f);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else {
            // This page is off-screen to the right
            view.setAlpha(0f);
        }
    }
}