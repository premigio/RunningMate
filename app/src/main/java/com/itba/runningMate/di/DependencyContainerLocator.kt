package com.itba.runningMate.di

import android.content.Context
import androidx.annotation.VisibleForTesting

object DependencyContainerLocator {

    private var dependencyContainer: DependencyContainer? = null

    @JvmStatic
    fun locateComponent(context: Context): DependencyContainer {
        if (dependencyContainer == null) {
            dependencyContainer = ProductionDependencyContainer(context)
        }
        return dependencyContainer!!
    }

    @VisibleForTesting
    fun setComponent(dependencyContainer: DependencyContainer?) {
        DependencyContainerLocator.dependencyContainer = dependencyContainer
    }
}