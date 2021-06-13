package com.itba.runningMate.mainpage.fragments.feed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.feed.cards.PastRunsCard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FeedFragment extends Fragment implements FeedView {

    private FeedPresenter presenter;

    private PastRunsCard pastRunsCard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DependencyContainer container = DependencyContainerLocator.locateComponent(view.getContext());

        pastRunsCard = view.findViewById(R.id.past_run_card);

        presenter = new FeedPresenter(this,
                container.getRunRepository(),
                container.getSchedulerProvider(),
                container.getCacheFileProvider(),
                pastRunsCard);

        pastRunsCard.setOnClickListener((v) -> presenter.goToPastRunsActivity());
        pastRunsCard.setPresenter(presenter);
    }

    @Override
    public void goToPastRunsActivity() {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("runningmate")
                .encodedAuthority("pastruns");
        startActivity(new Intent(Intent.ACTION_VIEW, uriBuilder.build()));
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onViewAttached();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onViewDetached();
    }

    @Override
    public void setPastRunCardsNoText() {
        pastRunsCard.setPastRunCardsNoText();
    }

    @Override
    public void addRunToCard(int i, Run run) {
        pastRunsCard.addRunToCard(i, run);
    }

    @Override
    public void disappearRuns(int i) {
        pastRunsCard.disappearRuns(i);
    }

    public void setPastRuns(List<Run> list) {
        pastRunsCard.setPastRuns(list);

    }
}
