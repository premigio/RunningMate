package com.itba.runningMate.utils.providers.files;

import android.net.Uri;

import java.io.File;

public interface CacheFileProvider {

    File getFile(String fileName);

    Uri getUriForFile(File file);

}
