package com.itba.runningMate.achievements.elements;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.itba.runningMate.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AchievementElementViewImpl extends FrameLayout implements AchievementsElementView{

    TextView titleView, descriptionView;
    ImageView imageView;

    public AchievementElementViewImpl(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.achievement_element, this);
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.achievement_element, this);
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.achievement_element, this);
    }

    public AchievementElementViewImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.achievement_element, this);
    }

    @Override
    public void bind(String title, String description) {
        //this.predicate = predicate;
        imageView = this.findViewById(R.id.badge_icon);

        titleView = this.findViewById(R.id.achievement_title_name);
        descriptionView = this.findViewById(R.id.achievement_description);

        titleView.setText(title);
        descriptionView.setText(description);

    }

    @Override
    public void setBadgeVisibility(boolean achieved) {
        if (achieved){
            imageView.setVisibility(VISIBLE);
        } else {
            imageView.setVisibility(INVISIBLE);
        }
    }
}
