package com.itba.runningMate.running.fragments.music

import com.spotify.sdk.android.authentication.AuthenticationRequest

interface RunningMusicView {
    fun openLoginActivity(requestCode: Int, request: AuthenticationRequest?)
    fun connectSpotify()
    fun changeButton(play:Boolean)
    fun setSongName(name: String)
}