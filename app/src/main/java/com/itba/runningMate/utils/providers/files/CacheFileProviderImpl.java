package com.itba.runningMate.utils.providers.files;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

import timber.log.Timber;

public class CacheFileProviderImpl implements CacheFileProvider {

    private final Context appContext;

    public CacheFileProviderImpl(final Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public File getFile(String fileName) {
        File file = new File(appContext.getCacheDir(), fileName);
        Timber.i("New file: %s created in cache", fileName);
        return file;
    }

    @Override
    public Uri getUriForFile(File file) {
        return FileProvider.getUriForFile(appContext, "com.itba.runningMate", file);
    }
}
