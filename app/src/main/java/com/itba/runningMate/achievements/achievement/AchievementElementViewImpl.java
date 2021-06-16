package com.itba.runningMate.achievements.achievement;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itba.runningMate.R;

public class AchievementElementViewImpl extends FrameLayout implements AchievementsElementView {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;

    public AchievementElementViewImpl(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.achievement_element, this);
        setUp();
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.achievement_element, this);
        setUp();
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.achievement_element, this);
        setUp();
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.achievement_element, this);
        setUp();
    }

    private void setUp() {
        imageView = this.findViewById(R.id.badge_icon);

        titleTextView = this.findViewById(R.id.achievement_title_name);
        descriptionTextView = this.findViewById(R.id.achievement_description);
    }

    @Override
    public void bind(String title, String description) {
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @Override
    public void setBadgeVisibility(boolean achieved) {
        if (achieved) {
            imageView.setAlpha(1.0F);
        }
    }
}
