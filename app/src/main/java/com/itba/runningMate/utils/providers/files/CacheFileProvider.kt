package com.itba.runningMate.utils.providers.files

import android.net.Uri
import java.io.File

interface CacheFileProvider {

    fun getFile(fileName: String): File

    fun getUriForFile(file: File): Uri
}