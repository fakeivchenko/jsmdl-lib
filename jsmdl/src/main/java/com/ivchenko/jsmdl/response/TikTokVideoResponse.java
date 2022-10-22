package com.ivchenko.jsmdl.response;

import com.ivchenko.jsmdl.video.TikTokVideo;

import java.util.List;

public class TikTokVideoResponse extends VideoResponse<TikTokVideo> {
    public TikTokVideoResponse(List<TikTokVideo> videos) {
        super(videos);
    }
}
