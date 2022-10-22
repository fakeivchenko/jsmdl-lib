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

package com.ivchenko.jsmdl.video;

import com.ivchenko.jsmdl.video.extra.Thumbnail;

import java.awt.*;

public class TikTokVideo extends Video implements Thumbnail {
    private final String thumbnailUrl;


    public TikTokVideo(Dimension resolution, String originalUrl, String downloadUrl, String thumbnailUrl) {
        super(resolution, originalUrl, downloadUrl);
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean hasThumbnail() {
        return thumbnailUrl != null;
    }
}
