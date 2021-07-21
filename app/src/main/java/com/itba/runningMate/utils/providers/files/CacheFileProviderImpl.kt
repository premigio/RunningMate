package com.itba.runningMate.utils.providers.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File

class CacheFileProviderImpl(context: Context) : CacheFileProvider {

    private val appContext: Context = context.applicationContext

    override fun getFile(fileName: String): File {
        val file = File(appContext.cacheDir, fileName)
        Timber.i("New file: %s created in cache", fileName)
        return file
    }

    override fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(appContext, "com.itba.runningMate", file)
    }

}