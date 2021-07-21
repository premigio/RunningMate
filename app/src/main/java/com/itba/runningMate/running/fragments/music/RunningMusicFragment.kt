package com.itba.runningMate.running.fragments.music

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itba.runningMate.R
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest

class RunningMusicFragment : Fragment(), RunningMusicView {

    private lateinit var pauseButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var nextButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var songName: TextView
    private lateinit var textToDownload: TextView
    private lateinit var spotifyLogo: ImageView
    private lateinit var presenter: RunningMusicPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createPresenter()
        pauseButton = view.findViewById(R.id.pause_music)
        playButton = view.findViewById(R.id.play_music)
        nextButton = view.findViewById(R.id.next_song)
        backButton = view.findViewById(R.id.back_song)
        songName = view.findViewById(R.id.song_name)
        textToDownload = view.findViewById(R.id.message_no_spotify)
        spotifyLogo = view.findViewById(R.id.spotify_logo)
        songName.ellipsize = TextUtils.TruncateAt.MARQUEE;
        songName.isSelected = true;
        songName.isSingleLine = true;
        setUpButtons()
    }

    override fun connectSpotify() {
        SpotifyAppRemote.connect(
            requireContext(),
            presenter.getSpotifyConnectParams(),
            object : ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                    presenter.spotifyConnected(spotifyAppRemote)
                }

                override fun onFailure(error: Throwable?) {
                    presenter.onSpotifyConnectFailure()
                }
            })

    }

    override fun changeButton(play:Boolean) {
        if (play) {
            playButton.hide()
            pauseButton.show()
        }else {
            pauseButton.hide()
            playButton.show()
        }
        changeConstraintLayout(play)
    }

    private fun changeConstraintLayout(play: Boolean) {
        val button : Int = if (play){
            R.id.pause_music
        }else {
            R.id.play_music
        }
        val constraintLayout: ConstraintLayout = requireActivity().findViewById(R.id.music_cl)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            R.id.back_song,
            ConstraintSet.RIGHT,
            button,
            ConstraintSet.LEFT,
            0
        )
        constraintSet.connect(
            R.id.next_song,
            ConstraintSet.LEFT,
            button,
            ConstraintSet.RIGHT,
            0
        )
        constraintSet.connect(
            R.id.song_name,
            ConstraintSet.BOTTOM,
            button,
            ConstraintSet.TOP,
            0
        )
        constraintSet.setVerticalBias(R.id.song_name, 0.811f)
        constraintSet.applyTo(constraintLayout)
    }

    private fun setUpButtons() {
        playButton.setOnClickListener { presenter.onPlayButtonClick() }
        pauseButton.setOnClickListener { presenter.onPauseButtonClick() }
        nextButton.setOnClickListener { presenter.onNextButtonClick() }
        backButton.setOnClickListener { presenter.onBackButtonClick() }
    }

    private fun createPresenter() {
        presenter = RunningMusicPresenter(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
    }

    override fun openLoginActivity(requestCode: Int, request: AuthorizationRequest?) {
        AuthorizationClient.stopLoginActivity(requireActivity(),requestCode)
        AuthorizationClient.openLoginActivity(requireActivity(), requestCode, request)
    }

    override fun setSongName(name: String) {
        songName.text = name
    }

    override fun disappearText() {
        spotifyLogo.visibility = View.GONE
        textToDownload.visibility = View.GONE
    }

}
