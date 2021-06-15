package com.itba.runningMate.achievements;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.achievements.elements.AchievementElementViewImpl;
import com.itba.runningMate.achievements.elements.Achievements;
import com.itba.runningMate.achievements.elements.AchievementsElementView;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.repository.achievementsstorage.AchievementsStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity implements AchievementsView{

    private AchievementsPresenter presenter;

    private TextView goalTitle;
    private TextView goalSubtitle;
    private ImageView goalImage;
    private ProgressBar progressBar;
    private List<AchievementsElementView> achievements;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        final DependencyContainer container = DependencyContainerLocator.locateComponent(this);
        final SchedulerProvider schedulerProvider = container.getSchedulerProvider();
        final RunRepository runRepository = container.getRunRepository();
        final AchievementsStorage stateStorage = container.getAchievementsStorage();

        presenter = new AchievementsPresenter(runRepository, schedulerProvider,stateStorage,this);

        goalTitle = findViewById(R.id.goal_title);
        goalSubtitle = findViewById(R.id.goal_subtitle);
        goalImage = findViewById(R.id.goal_image);
        progressBar = findViewById(R.id.goal_progress_bar);

        achievements = new ArrayList<>();
        AchievementsElementView achievement = findViewById(R.id.achievement1);
        achievement.bind(getString(R.string.total_distance_achievement_title),
                         getString(R.string.total_distance_achievement_subtitle));
        achievements.add(achievement);

        achievement = findViewById(R.id.achievement2);
        achievement.bind(getString(R.string.max_kcal_achievement_title),
                getString(R.string.max_kcal_achievement_subtitle));
        achievements.add(achievement);

        achievement = findViewById(R.id.achievement3);
        achievement.bind(getString(R.string.max_speed_achievement_title),
                getString(R.string.max_speed_achievement_subtitle));
        achievements.add(achievement);

        achievement = findViewById(R.id.achievement4);
        achievement.bind(getString(R.string.max_time_achievement_title),
                getString(R.string.max_time_achievement_subtitle));
        achievements.add(achievement);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onViewAttached();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onViewDetached();
    }

    @Override
    public void setGoalTitle(int title) {
        goalTitle.setText(title);
    }

    @Override
    public void setGoalSubtitle(int subtitle) {
        goalSubtitle.setText(subtitle);
    }

    @Override
    public void setGoalImage(int image) {
        goalImage.setImageResource(image);
    }

    @Override
    public void setProgressBar(double distance, double max) {
        progressBar.setMax((int) max);
        progressBar.setProgress((int) distance);
    }

    @Override
    public void setAchievement(Achievements achievementNumber, boolean achieved) {
        achievements.get(achievementNumber.ordinal()).setBadgeVisibility(achieved);
    }
}
