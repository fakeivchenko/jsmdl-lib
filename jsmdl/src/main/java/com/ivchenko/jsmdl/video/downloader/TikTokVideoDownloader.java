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

package com.ivchenko.jsmdl.video.downloader;

import com.google.api.client.http.HttpRequestFactory;
import com.ivchenko.jsmdl.video.TikTokVideo;
import com.ivchenko.jsmdl.video.Video;

import java.io.File;
import java.io.InputStream;

public class TikTokVideoDownloader extends BasicVideoDownloader {
    public TikTokVideoDownloader(HttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    public Class<? extends Video> accepts() {
        return TikTokVideo.class;
    }
}
