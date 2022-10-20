package com.ivchenko.jsmdl.video.downloader;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Preconditions;
import com.ivchenko.jsmdl.video.Video;

import java.io.*;

public class BasicVideoDownloader implements VideoDownloader {
    protected final HttpRequestFactory requestFactory;

    public BasicVideoDownloader(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    @Override
    public Class<? extends Video> accepts() {
        return Video.class;
    }

    @Override
    public InputStream getInputStream(Video video) {
        try {
            return getResponse(video.getDownloadUrl()).getContent();
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadToFile(Video video, File file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            getResponse(video.getDownloadUrl()).download(outputStream);
        } catch (FileNotFoundException e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        }
    }

    private HttpResponse getResponse(String downloadUrl) throws IOException {
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(downloadUrl));
        HttpResponse response = request.execute();
        Preconditions.checkState(response.isSuccessStatusCode());
        return response;
    }
}
