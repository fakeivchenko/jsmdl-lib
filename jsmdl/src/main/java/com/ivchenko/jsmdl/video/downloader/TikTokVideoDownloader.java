package com.ivchenko.jsmdl.video.downloader;

import com.google.api.client.http.HttpRequestFactory;
import com.ivchenko.jsmdl.video.TikTokVideo;
import com.ivchenko.jsmdl.video.Video;

import java.io.File;
import java.io.InputStream;

public class TikTokVideoDownloader extends BasicVideoDownloader {
    public TikTokVideoDownloader(HttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    public Class<? extends Video> accepts() {
        return TikTokVideo.class;
    }
}
