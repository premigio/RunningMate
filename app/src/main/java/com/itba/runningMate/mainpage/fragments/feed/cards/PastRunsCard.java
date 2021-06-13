package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.feed.FeedPresenter;
import com.itba.runningMate.pastruns.runs.ui.OnRunClickListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class PastRunsCard extends CardView {

    private FeedPresenter presenter;

    private TextView pastRunsEmptyMessage;
    private List<RunElementView> runs;
    private Button seeAll;

    private WeakReference<OnRunClickListener> listener;

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
        seeAll.setOnClickListener((v) -> presenter.goToPastRunsActivity());
    }

    public PastRunsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        prepareFromConstructor(context);
    }

    public PastRunsCard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareFromConstructor(context);
    }

    public void setPresenter(FeedPresenter fp) {
        presenter = fp;
    }

    public void setPastRunCardsNoText() {
        pastRunsEmptyMessage.setVisibility(View.VISIBLE);
    }

    public void disappearNoText() {
        pastRunsEmptyMessage.setVisibility(View.GONE);
    }

    public void addRunToCard(int i, Run run) {
        runs.get(i).setVisibility(VISIBLE);
        runs.get(i).setPresenter(presenter);
        runs.get(i).bind(run);
        runs.get(i).setOnClickListener((v) -> presenter.onPastRunClick(run.getUid()));
    }

    public void disappearRuns(int i) {
        if (i >= 3) return;
        while (i < 3) {
            runs.get(i++).setVisibility(GONE);
        }
    }

//    public void setRunDetailsListener(OnClickListener listener){
//        this.listener = new WeakReference<>(listener);
//    }

    public void launchRunDetails(long id) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("runningmate")
                .encodedAuthority("run")
                .appendQueryParameter("run-id", Long.toString(id));
        getContext().startActivity(new Intent(Intent.ACTION_VIEW, uriBuilder.build()));
    }

}
