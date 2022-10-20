package com.ivchenko.jsmdl.video;

import com.ivchenko.jsmdl.video.extra.Thumbnail;

import java.awt.*;

public class TwitterVideo extends Video implements Thumbnail {
    private final String thumbnailUrl;

    public TwitterVideo(Dimension resolution, String originalUrl, String downloadUrl, String thumbnailUrl) {
        super(resolution, originalUrl, downloadUrl);
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean hasThumbnail() {
        return thumbnailUrl == null;
    }
}
