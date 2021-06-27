package com.itba.runningMate.rundetails

import android.graphics.Bitmap
import android.net.Uri
import com.itba.runningMate.domain.Route
import com.itba.runningMate.rundetails.model.RunMetricsDetail

interface RunDetailsView {

    fun showRoute(route: Route)

    fun showRunMetrics(runMetrics: RunMetricsDetail)

    fun endActivity()

    fun shareImageIntent(uri: Uri)

    fun getMetricsImage(detail: RunMetricsDetail?): Bitmap?

    fun showShareRunError()

    fun showUpdateTitleError()

    fun showDeleteError()

    fun showRunNotAvailableError()

}