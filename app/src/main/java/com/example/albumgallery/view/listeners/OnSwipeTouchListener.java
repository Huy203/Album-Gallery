package com.example.albumgallery.view.listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.example.albumgallery.R;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final View view;
    private final Animation slideInLeft;
    private final Animation slideInRight;

    public OnSwipeTouchListener(Context context, View view) {
        this.view = view;
        gestureDetector = new GestureDetector(context, new GestureListener());
        slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float diffX = 0;
            if (e1 != null) {
                diffX = e2.getX() - e1.getX();
            }
            float diffY = 0;
            if (e1 != null) {
                diffY = e2.getY() - e1.getY();
            }
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                        view.startAnimation(slideInLeft);
                    } else {
                        onSwipeLeft();
                        view.startAnimation(slideInRight);
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
