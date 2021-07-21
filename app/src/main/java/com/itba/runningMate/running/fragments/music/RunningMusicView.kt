package com.itba.runningMate.running.fragments.music

import com.spotify.sdk.android.auth.AuthorizationRequest


interface RunningMusicView {
    fun openLoginActivity(requestCode: Int, request: AuthorizationRequest?)
    fun connectSpotify()
    fun changeButton(play:Boolean)
    fun setSongName(name: String)
    fun disappearText()
}