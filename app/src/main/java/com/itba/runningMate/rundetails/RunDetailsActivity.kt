package com.itba.runningMate.rundetails

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.domain.Route
import com.itba.runningMate.map.Map
import com.itba.runningMate.rundetails.model.RunMetricsDetail
import com.itba.runningMate.utils.ImageProcessing

class RunDetailsActivity : AppCompatActivity(), RunDetailsView, OnMapReadyCallback {

    private lateinit var mapView: Map
    private lateinit var runTimeInterval: TextView
    private lateinit var elapsedTime: TextView
    private lateinit var runningTime: TextView
    private lateinit var speed: TextView
    private lateinit var pace: TextView
    private lateinit var distance: TextView
    private lateinit var calories: TextView
    private lateinit var title: EditText
    private lateinit var presenter: RunDetailsPresenter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_details)
        val intent = intent
        val uri = intent.data
        val runIdString = uri?.getQueryParameter(RUN_ID)
        val id: Long = runIdString!!.toLong(10)
        createPresenter(id)
        setUpMap(savedInstanceState)
        setUp()
    }

    private fun setUpMap(savedInstanceState: Bundle?) {
        mapView = findViewById(R.id.run_detail_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun setUp() {
        speed = findViewById(R.id.speed)
        pace = findViewById(R.id.pace)
        distance = findViewById(R.id.distance)
        title = findViewById(R.id.run_detail_title)
        runTimeInterval = findViewById(R.id.run_detail_run_time_interval)
        runningTime = findViewById(R.id.running_time)
        elapsedTime = findViewById(R.id.elapsed_time)
        calories = findViewById(R.id.calories)


        //Creo el botón para volver
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        val deleteBtn = findViewById<Button>(R.id.btn_run_detail_delete)
        deleteBtn.setOnClickListener { deleteConfirmationMessage() }
        val shareBtn = findViewById<Button>(R.id.btn_run_detail_share)
        shareBtn.setOnClickListener { presenter.onShareButtonClick() }
        title.setRawInputType(InputType.TYPE_CLASS_TEXT)
        title.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE)
        title.setImeOptions(EditorInfo.IME_ACTION_DONE)
        title.setOnEditorActionListener { textView: TextView, actionId: Int, event: KeyEvent? ->
            onTextEditAction(
                textView,
                actionId,
                event
            )
        }
    }

    private fun onTextEditAction(textView: TextView, actionId: Int, event: KeyEvent?): Boolean {
        /* Ref: https://gist.github.com/Dogesmith/2b98df97b4fca849ff94 */
        if (event == null) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                title.clearFocus()
                val inputMethodManager =
                    this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    textView.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                presenter.onRunTitleModified(textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Capture soft enters in other singleLine EditTexts
            } else if (actionId == EditorInfo.IME_ACTION_GO) {
            } else {
                // Let the system handle all other null KeyEvents
                return false
            }
        } else if (actionId == EditorInfo.IME_NULL) {
            /*  Capture most soft enters in multi-line EditTexts and all hard enters;
            They supply a zero actionId and a valid keyEvent rather than
            a non-zero actionId and a null event like the previous cases.*/
            if (event.action == KeyEvent.ACTION_DOWN) {
                /*We capture the event when the key is first pressed.*/
            } else {
                return true
            }
        } else {
            /*We let the system handle it when the listener is triggered by something that
            wasn't an enter.*/
            return false
        }
        return true
    }

    private fun deleteConfirmationMessage() {
        val alertBox = AlertDialog.Builder(this)
        alertBox.setMessage(R.string.run_delete_message)
            .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int -> presenter.onDeleteButtonClick() }
            .setNegativeButton(R.string.no) { _: DialogInterface?, _: Int -> }
            .show()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        presenter.onViewAttached()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
        mapView.onStop()
    }

    private fun createPresenter(runId: Long) {
        val container = DependencyContainerLocator.locateComponent(this)
        val schedulerProvider = container.getSchedulerProvider()
        val runRepository = container.getRunRepository()
        val cacheFileProvider = container.getCacheFileProvider()
        val achievementsStorage = container.getAchievementsStorage()
        presenter = RunDetailsPresenter(
            cacheFileProvider,
            runRepository,
            schedulerProvider,
            achievementsStorage,
            runId,
            this
        )
    }

    override fun endActivity() {
        finish()
    }

    override fun showRunMetrics(runMetrics: RunMetricsDetail) {
        speed.text = runMetrics.speed
        pace.text = runMetrics.pace
        distance.text = runMetrics.distance
        runTimeInterval.text = runMetrics.runTimeInterval
        elapsedTime.text = runMetrics.elapsedTime
        title.setText(runMetrics.title)
        runningTime.text = runMetrics.runningTime
        calories.text = runMetrics.calories
    }

    override fun showRoute(route: Route) {
        mapView.showRouteWithMarker(route)
        mapView.centerMapOn(route)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.bind(googleMap)
        presenter.onMapAttached()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun shareImageIntent(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share Via"))
    }

    override fun getMetricsImage(detail: RunMetricsDetail): Bitmap {
        val s = RunSummary(this)
        s.bind(detail)
        return ImageProcessing.createBitmapFromView(s, 390, 330)
    }

    override fun showShareRunError() {
        Toast.makeText(this, "Error while attempting to share run", Toast.LENGTH_LONG).show()
    }

    override fun showUpdateTitleError() {
        Toast.makeText(this, "Error while attempting to update title", Toast.LENGTH_LONG).show()
    }

    override fun showDeleteError() {
        Toast.makeText(this, "Error while attempting to delete run", Toast.LENGTH_LONG).show()
    }

    override fun showRunNotAvailableError() {
        Toast.makeText(this, "Error while attempting to retrieve run", Toast.LENGTH_LONG).show()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        private const val RUN_ID = "run-id"
    }
}