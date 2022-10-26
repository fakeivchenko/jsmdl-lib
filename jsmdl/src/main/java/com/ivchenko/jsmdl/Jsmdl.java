/*
 * Copyright 2022 Ivchenko Anton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivchenko.jsmdl;

import com.google.api.client.http.GenericUrl;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.VideoResponse;
import com.ivchenko.jsmdl.servicehandler.ServiceHandler;
import com.ivchenko.jsmdl.video.Video;
import com.ivchenko.jsmdl.video.downloader.BasicVideoDownloader;
import com.ivchenko.jsmdl.video.downloader.VideoDownloader;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.InputStream;
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
        VideoDownloader videoDownloader = getDownloaderForVideo(video);
        videoDownloader.downloadToFile(video, file);
    }

    public void downloadVideoToFile(Video video, String file) {
        downloadVideoToFile(video, new File(file));
    }

    public InputStream getVideoInputStream(Video video) {
        VideoDownloader videoDownloader = getDownloaderForVideo(video);
        return videoDownloader.getInputStream(video);
    }

    private VideoDownloader getDownloaderForVideo(Video video) {
        Optional<ServiceHandler<?>> serviceHandlerOptional = config.getServiceHandlers().stream().filter(
                sh -> sh.getVideoDownloader().accepts().equals(video.getClass())
        ).findAny();

        // If ServiceHandler present return it's VideoDownloader, otherwise return BasicVideoDownloader
        if (serviceHandlerOptional.isPresent())
            return serviceHandlerOptional.get().getVideoDownloader();
        else
            return new BasicVideoDownloader(config.getRequestFactory());
    }
}
