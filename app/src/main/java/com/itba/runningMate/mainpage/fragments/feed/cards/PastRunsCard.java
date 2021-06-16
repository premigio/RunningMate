package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.feed.run.OnRunClickListener;
import com.itba.runningMate.mainpage.fragments.feed.run.RunElementView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PastRunsCard extends CardView {

    private TextView pastRunsEmptyMessage;
    private List<RunElementView> runs;
    private Button seeAll;

    private WeakReference<OnRunClickListener> runElementListener;
    private WeakReference<OnSeeAllClickListener> onSeeAllClickListener;

    public PastRunsCard(@NonNull @NotNull Context context) {
        super(context);
        prepareFromConstructor(context);
    }

    private void prepareFromConstructor(Context context) {
        inflate(context, R.layout.card_past_runs, this);
        runs = new ArrayList<>();

        runs.add(findViewById(R.id.past_run_card_1));
        runs.add(findViewById(R.id.past_run_card_2));
        runs.add(findViewById(R.id.past_run_card_3));

        pastRunsEmptyMessage = findViewById(R.id.past_run_empty_card);
        seeAll = findViewById(R.id.see_all_past_runs);
        seeAll.setOnClickListener(l -> onSeeAllButtonClicked());
    }

    public PastRunsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        prepareFromConstructor(context);
    }

    public PastRunsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareFromConstructor(context);
    }

    public void setPastRunCardsNoText() {
        pastRunsEmptyMessage.setVisibility(View.VISIBLE);
    }

    public void disappearNoText() {
        pastRunsEmptyMessage.setVisibility(View.GONE);
    }

    public void addRunToCard(int i, Run run) {
        runs.get(i).setVisibility(VISIBLE);
        if (runElementListener.get() != null) {
            runs.get(i).setOnClick(runElementListener.get());
        }
        runs.get(i).bind(run);
    }

    public void disappearRuns(int i) {
        if (i >= 3) return;
        while (i < 3) {
            runs.get(i++).setVisibility(GONE);
        }
    }

    private void onSeeAllButtonClicked() {
        if (onSeeAllClickListener.get() != null) {
            onSeeAllClickListener.get().onSeeAllPastRunsClick();
        }
    }

    public void setElementListener(OnRunClickListener feedFragment) {
        this.runElementListener = new WeakReference<>(feedFragment);
    }

    public void setSeeAllListener(OnSeeAllClickListener onSeeAllClickListener) {
        this.onSeeAllClickListener = new WeakReference<>(onSeeAllClickListener);
    }
}
