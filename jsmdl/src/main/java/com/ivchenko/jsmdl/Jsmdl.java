package com.ivchenko.jsmdl;

import com.google.api.client.http.GenericUrl;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.VideoResponse;
import com.ivchenko.jsmdl.servicehandler.ServiceHandler;
import com.ivchenko.jsmdl.video.Video;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class Jsmdl {
    // Config to be used in this Jsmdl instance
    @Getter private final JsmdlConfig config;

    public Jsmdl(@NonNull JsmdlConfig config) {
        this.config = config;
    }

    public Jsmdl() {
        this.config = JsmdlConfig.buildDefault();
    }

    public VideoResponse<?> getVideoResponse(VideoRequest videoRequest) {
        String requestHost = new GenericUrl(videoRequest.getVideoUrl()).getHost();

        Optional<ServiceHandler<?>> serviceHandlerOptional = config.getServiceHandlers().stream().filter(
                sh -> Arrays.stream(sh.getSupportedRequestHosts()).anyMatch(h -> h.equals(requestHost.toLowerCase()))
        ).findAny();

        if (serviceHandlerOptional.isEmpty()) throw new RuntimeException(); // TODO: 10/21/22 Replace
        else return serviceHandlerOptional.get().execute(videoRequest);
    }

    public void downloadVideoToFile(Video video, File file) {
        String host = new GenericUrl(video.getOriginalUrl()).getHost();

        Optional<ServiceHandler<?>> serviceHandlerOptional = config.getServiceHandlers().stream().filter(
                sh -> sh.getVideoDownloader().accepts().equals(video.getClass())
        ).findAny();

        if (serviceHandlerOptional.isEmpty()) throw new RuntimeException(); // TODO: 10/21/22 Replace
        else serviceHandlerOptional.get().getVideoDownloader().downloadToFile(video, file);
    }

    public void downloadVideoToFile(Video video, String file) {
        downloadVideoToFile(video, new File(file));
    }
}
