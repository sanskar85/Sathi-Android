package com.abbvmk.sathi.Views.ProgressButton;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.abbvmk.sathi.R;

public class ProgressButton extends ConstraintLayout {

    public interface OnClickListener {
        void onClick();
    }

    private OnClickListener onClickListener;


    private CardView cardView;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView textView;


    private boolean enabled;

    Animation fade_in;


    public ProgressButton(Context context) {
        super(context);
        init(context);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttributes(context, attrs);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_progress_button, this, true);
        cardView = view.findViewById((R.id.card_view));
        layout = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);
        setViewEnabled(true);
        cardView.setOnClickListener((v) -> {
            if (onClickListener != null) {
                onClickListener.onClick();
            }
        });
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        int backgroundColor;
        int progressColor;
        int textColor;
        float cardRadius;
        String text;
        backgroundColor = a.getColor(R.styleable.ProgressButton_backgroundColor, ContextCompat.getColor(context, R.color.progress_bar_background));
        progressColor = a.getColor(R.styleable.ProgressButton_progressColor, ContextCompat.getColor(context, R.color.white));
        textColor = a.getColor(R.styleable.ProgressButton_textColor, ContextCompat.getColor(context, R.color.white));
        text = a.getString(R.styleable.ProgressButton_text);
        cardRadius = a.getDimension(R.styleable.ProgressButton_cardRadius, 10);
        a.recycle();
        setCardBackgroundColor(backgroundColor);
        setProgressColor(progressColor);
        setTextColor(textColor);
        setText(text);
        setCardRadius(cardRadius);
    }


    public void buttonActivated() {
        new Handler(Looper.getMainLooper()).post(
                () -> {
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText(R.string.please_wait);
                });
    }

    public void buttonFinished() {
        new Handler(Looper.getMainLooper()).post(
                () -> {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("");
                });
    }

    public void buttonFinished(String text) {
        new Handler(Looper.getMainLooper()).post(
                () -> {
                    progressBar.setVisibility(View.GONE);
                    textView.setText(text);
                });
    }

    public void setViewEnabled(boolean enabled) {
        new Handler(Looper.getMainLooper()).post(
                () -> {
                    if (enabled) {
                        cardView.setAlpha(1);
                    } else {
                        cardView.setAlpha((float) 0.7);
                    }
                });

        this.enabled = enabled;
    }

    public boolean isViewEnabled() {
        return enabled;
    }

    public void setCardBackgroundColor(int backgroundColor) {
        new Handler(Looper.getMainLooper()).post(
                () -> {
                    cardView.setCardBackgroundColor(backgroundColor);
                });
    }

    public void setProgressColor(int progressColor) {
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(progressColor));
    }

    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setCardRadius(float radius) {
        cardView.setRadius(radius);
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }
}
