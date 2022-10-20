package com.ivchenko.jsmdl.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public abstract class VideoRequest {
    private String videoUrl;

    public VideoRequest(@NonNull String videoUrl) {
        this.videoUrl = validUrl(videoUrl);
    }

    protected String validUrl(String videoUrl) {
        // TODO: 10/20/22 Implement more validation
        return addProtocolIfAbsent(videoUrl);
    }

    private String addProtocolIfAbsent(String videoURL) {
        if (!videoURL.matches("^.{0,5}://.+$")) {
            return "http://" + videoURL;
        } else return videoURL;
    }
}
