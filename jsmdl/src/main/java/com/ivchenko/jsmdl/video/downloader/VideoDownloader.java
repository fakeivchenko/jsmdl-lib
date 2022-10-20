package com.ivchenko.jsmdl.video.downloader;

import com.ivchenko.jsmdl.video.Video;

import java.io.File;
import java.io.InputStream;

public interface VideoDownloader {
    Class<? extends Video> accepts();

    InputStream getInputStream(Video video);

    void downloadToFile(Video video, File file);

    default void downloadToFile(Video video, String file) {
        downloadToFile(video, new File(file));
    }
}
