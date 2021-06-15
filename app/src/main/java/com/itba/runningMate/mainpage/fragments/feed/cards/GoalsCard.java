package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.mainpage.fragments.feed.FeedFragment;
import com.itba.runningMate.mainpage.fragments.feed.FeedPresenter;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class GoalsCard extends CardView {

    ImageView image;
    TextView title, subtitle;
    Button seeAll;
    private OnSeeAllClickListener onSeeAllClickListener;


    public GoalsCard(@NonNull @NotNull Context context) {
        super(context);
        prepareFromConstructor(context);
    }

    public GoalsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        prepareFromConstructor(context);
    }

    public GoalsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareFromConstructor(context);
    }

    private void prepareFromConstructor(Context context) {
        inflate(context, R.layout.card_goals, this);

        image = findViewById(R.id.goal_image_card);
        title = findViewById(R.id.goal_title_card);
        subtitle = findViewById(R.id.goal_subtitle_card);
        seeAll = findViewById(R.id.see_all_goals);

        seeAll.setOnClickListener((v) -> this.onSeeAllClickListener.onSeeAllClickAchievements());
    }

    public void setTitle(int titleInt) {
        title.setText(titleInt);
    }

    public void setSubtitle(int subtitleInt) {
        subtitle.setText(subtitleInt);
    }

    public void setImage(int imageInt) {
        image.setImageResource(imageInt);
    }

    public void setSeeAllListener(OnSeeAllClickListener onSeeAllClickListener) {
        this.onSeeAllClickListener = onSeeAllClickListener;
    }
}
