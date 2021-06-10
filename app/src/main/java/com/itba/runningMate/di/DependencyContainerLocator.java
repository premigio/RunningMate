package com.itba.runningMate.di;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

public class DependencyContainerLocator {

    private static DependencyContainer dependencyContainer;

    private DependencyContainerLocator() {
    }

    public static DependencyContainer locateComponent(final Context context) {
        if (dependencyContainer == null) {
            dependencyContainer = new ProductionDependencyContainer(context);
        }
        return dependencyContainer;
    }

    @VisibleForTesting
    public static void setComponent(final DependencyContainer dependencyContainer) {
        DependencyContainerLocator.dependencyContainer = dependencyContainer;
    }
}
