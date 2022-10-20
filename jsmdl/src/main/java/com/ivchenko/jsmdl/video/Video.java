package com.ivchenko.jsmdl.video;

import lombok.Getter;

import java.awt.*;

public abstract class Video {
    @Getter protected final Dimension resolution;
    @Getter protected final String originalUrl;
    @Getter protected final String downloadUrl;

    public Video(Dimension resolution, String originalUrl, String downloadUrl) {
        this.resolution = resolution;
        this.originalUrl = originalUrl;
        this.downloadUrl = downloadUrl;
    }
}
