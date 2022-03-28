package com.abbvmk.sathi.Views.Loading;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abbvmk.sathi.R;

public class Loading extends ConstraintLayout {

    View view;
    ImageView image;

    public Loading(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Loading(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_loading, this, true);
        image = view.findViewById(R.id.image);
        view.setVisibility(GONE);
    }

    public void setProgressVisible(boolean visible) {

        new Handler(Looper.getMainLooper()).post(
                () -> {
                    if (visible) {
                        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(2000);
                        rotate.setInterpolator(new LinearInterpolator());
                        rotate.setRepeatCount(Animation.INFINITE);
                        image.startAnimation(rotate);
                        view.setVisibility(VISIBLE);
                    } else {
                        image.clearAnimation();
                        view.setVisibility(GONE);
                    }
                });
    }

    public boolean isProgressVisible() {
        return view.getVisibility() == VISIBLE;
    }

}
