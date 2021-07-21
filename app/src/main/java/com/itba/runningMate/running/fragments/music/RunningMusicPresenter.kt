package com.itba.runningMate.running.fragments.music

import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import timber.log.Timber
import java.lang.ref.WeakReference

class RunningMusicPresenter(view: RunningMusicView?) {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val REQUEST_CODE = 1337
    private val REDIRECT_URI = "runningmate://running"
    private val CLIENT_ID = "09b7faf352454d0a86db9ea9e5e2a43f"

    private val view: WeakReference<RunningMusicView> = WeakReference(view)

    fun onPlayButtonClick() {
        if (view.get() == null) {
            return
        }
        spotifyAppRemote?.playerApi?.resume()
        view.get()!!.changeButton(true)
    }

    fun onPauseButtonClick() {
        spotifyAppRemote?.playerApi?.pause()
        if (view.get() == null) {
            return
        }
        view.get()!!.changeButton(false)
    }

    fun onNextButtonClick() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun onBackButtonClick() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun onViewAttached() {
        if (view.get() == null) {
            return
        }
        view.get()!!.connectSpotify()
    }

    fun onViewDetached() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    fun getSpotifyConnectParams(): ConnectionParams? {
        return ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
    }

    fun onSpotifyConnectFailure(error: Throwable?) {
        Timber.d("Could not connect to Spotify")
        loginSpotify()
    }

    private fun loginSpotify() {
        if (view.get() == null) {
            return
        }

        val builder = AuthenticationRequest.Builder(
            CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI
        )

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        view.get()!!.openLoginActivity(REQUEST_CODE,request)
    }

    fun spotifyConnected(spotifyAppRemote: SpotifyAppRemote?) {
        this.spotifyAppRemote = spotifyAppRemote
        this.spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            var name = "No song selected"
            if (it.track?.name != null && it.track?.artist?.name != null)
                name = it.track?.name + " - " + it.track?.artist?.name
            view.get()!!.setSongName(name)
            //view.get()!!.changeButton(!it.isPaused)

        }
    }
}
