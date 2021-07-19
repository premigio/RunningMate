package com.itba.runningMate.running.fragments.metrics

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.TrackingService
import com.itba.runningMate.utils.Formatters
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RunningMetricsFragment : Fragment(), RunningMetricsView, ServiceConnection {

    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE: Int = 101

    private lateinit var pauseButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var stopButton: FloatingActionButton
    private lateinit var stopWatch: TextView
    private lateinit var distance: TextView
    private lateinit var calories: TextView
    private lateinit var pace: TextView
    private lateinit var presenter: RunningMetricsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_running_metrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createPresenter()
        pauseButton = view.findViewById(R.id.pause)
        playButton = view.findViewById(R.id.play)
        stopButton = view.findViewById(R.id.stop)
        distance = view.findViewById(R.id.distance)
        pace = view.findViewById(R.id.pace)
        calories = view.findViewById(R.id.calories)
        stopWatch = view.findViewById(R.id.running_time)
        setUpButtons()
    }

    private fun setUpButtons() {
        stopButton.setOnLongClickListener {
            presenter.onStopButtonClick()
            true
        }
        enlargeOnTouch(stopButton)
        pauseButton.setOnClickListener { presenter.onPauseButtonClick() }
        playButton.setOnClickListener { presenter.onPlayButtonClick() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enlargeOnTouch(btn: FloatingActionButton) {
        btn.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = 1.25.toFloat()
                val y = 1.25.toFloat()
                btn.scaleX = x
                btn.scaleY = y
            } else if (event.action == MotionEvent.ACTION_UP) {
                val x = 1f
                val y = 1f
                btn.scaleX = x
                btn.scaleY = y
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()

        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()

        presenter.onViewDetached()
    }

    override fun attachTrackingService() {
        val intent = Intent(activity, TrackingService::class.java)
        this.requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun detachTrackingService() {
        this.requireActivity().unbindService(this)
        presenter.onTrackingServiceDetached()
    }

    private fun createPresenter() {
        val container = DependencyContainerLocator.locateComponent(requireContext())
        val schedulerProvider = container.getSchedulerProvider()
        val runRepository = container.getRunRepository()
        val achievementsStorage = container.getAchievementsStorage()
        presenter =
            RunningMetricsPresenter(runRepository, schedulerProvider, achievementsStorage, this)
    }

    override fun updateDistance(elapsedDistance: Float) {
        distance.text =
            twoDecimalPlacesFormatter.format(elapsedDistance.toDouble())
    }

    override fun updateCalories(calories: Int) {
        this.calories.text = calories.toString()
    }

    override fun updateStopwatch(elapsedTime: Long) {
        stopWatch.text = Formatters.hmsTimeFormatter(elapsedTime)
    }

    override fun updatePace(pace: Long) {
        this.pace.text = paceFormatter.format(Date(pace))
    }

    override fun showInitialMetrics() {
        pace.setText(R.string.text_view_running_initial_pace)
        distance.setText(R.string.text_view_running_initial_distance)
        stopWatch.setText(R.string.text_view_running_initial_time)
        calories.setText(R.string.text_view_running_initial_calories)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val tracker = service as Tracker
        presenter.onTrackingServiceAttached(tracker)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        presenter.onTrackingServiceDetached()
    }

    override fun showSaveRunError() {
        Toast.makeText(this.activity, getText(R.string.toast_error_run_save), Toast.LENGTH_LONG)
            .show()
    }

    override fun launchRunActivity(runId: Long) {
        val uri = Uri.Builder().scheme("runningmate")
            .authority("run")
            .appendQueryParameter("run-id", runId.toString()).build()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
        this.requireActivity().finish()
    }

    override fun finishActivity() {
        this.requireActivity().finish()
    }

    override fun showStopConfirm() {
        val alertBox = AlertDialog.Builder(
            this.requireActivity()
        )
        alertBox.setMessage(R.string.stop_run_message)
            .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int -> presenter.stopRun() }
            .setNegativeButton(R.string.no) { _: DialogInterface?, _: Int -> }
            .show()
    }

    override fun showStopBtn() {
        stopButton.visibility = View.VISIBLE
    }

    override fun showPlayBtn() {
        playButton.visibility = View.VISIBLE
    }

    override fun showPauseBtn() {
        pauseButton.visibility = View.VISIBLE
    }

    override fun hideStopBtn() {
        stopButton.visibility = View.INVISIBLE
    }

    override fun hidePlayBtn() {
        playButton.visibility = View.INVISIBLE
    }

    override fun hidePauseBtn() {
        pauseButton.visibility = View.INVISIBLE
    }

    override fun buildFitness(): FitnessOptions {
        return FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ).build()
    }

    override fun getFitAccount(fitApiClient: FitnessOptions): GoogleSignInAccount {
        return GoogleSignIn.getAccountForExtension(this.requireContext(), fitApiClient)
    }

    override fun isUserLoggedFit(
        account: GoogleSignInAccount,
        fitApiClient: FitnessOptions
    ): Boolean {
        return !GoogleSignIn.hasPermissions(account, fitApiClient)
    }

    override fun getGoogleFitPermissions(
        account: GoogleSignInAccount,
        fitApiClient: FitnessOptions
    ) {
        GoogleSignIn.requestPermissions(
            this,
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
            account,
            fitApiClient
        )
    }

    fun setStepCount(stepCount: Int, startTime: Long, endTime: Long): DataSet {
        val ds = DataSource.Builder().setAppPackageName(this.requireContext())
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setStreamName("GF - StepCount")
            .setType(DataSource.TYPE_RAW)
            .build()

        val dp = DataPoint.builder(ds).setField(Field.FIELD_STEPS, stepCount)
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS).build()

        return DataSet.builder(ds)
            .add(dp)
            .build()
    }

    fun insertData(ds: DataSet): Boolean {
        var success = false
        Fitness.getHistoryClient(this.requireContext(), getFitAccount(buildFitness()))
            .insertData(ds)
            .addOnCompleteListener { success = true }
            .addOnCanceledListener { success = false }
            .addOnFailureListener { success = false }
        return success
    }

    override fun getStepCount(startTime: Long, endTime: Long){
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()
        val account = getFitAccount(buildFitness())
        Fitness.getHistoryClient(this.requireContext(), account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                Timber.d("---> %s",response.buckets.flatMap { it.dataSets }[0])
            }
            .addOnFailureListener { Timber.d("Could not retrieve steps") }
    }

    fun getWeight(): Task<DataReadResponse> {
        val readReq = DataReadRequest.Builder().aggregate(DataType.TYPE_WEIGHT)
            .setTimeRange(1, System.currentTimeMillis(), TimeUnit.MILLISECONDS).build()
        return Fitness.getHistoryClient(this.requireContext(), getFitAccount(buildFitness()))
            .readData(readReq)
    }

    companion object {
        private val paceFormatter = SimpleDateFormat("mm'' ss'\"'", Locale.getDefault())
        private val twoDecimalPlacesFormatter = DecimalFormat("0.00")
    }
}