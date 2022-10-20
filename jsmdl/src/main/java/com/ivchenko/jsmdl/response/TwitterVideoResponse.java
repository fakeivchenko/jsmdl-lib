package com.ivchenko.jsmdl.response;

import com.ivchenko.jsmdl.video.TwitterVideo;

import java.util.List;

public class TwitterVideoResponse extends VideoResponse<TwitterVideo> {
    public TwitterVideoResponse(List<TwitterVideo> videos) {
        super(videos);
    }
}
