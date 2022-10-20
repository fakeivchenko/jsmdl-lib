package com.ivchenko.jsmdl.response;

import com.ivchenko.jsmdl.video.Video;

import java.util.List;

public abstract class VideoResponse<VID extends Video> {
    private final List<VID> videos;

    public VideoResponse(List<VID> videos) {
        this.videos = videos;
    }

    public List<VID> getAllVideos() {
        return videos;
    }

    // TODO: 10/20/22 Implement
    public VID getBestVideo() {
        return getAllVideos().stream()
                .findAny()
                .orElse(null);
    }
}
