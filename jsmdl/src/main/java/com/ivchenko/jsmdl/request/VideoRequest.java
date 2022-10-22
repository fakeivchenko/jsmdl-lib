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
