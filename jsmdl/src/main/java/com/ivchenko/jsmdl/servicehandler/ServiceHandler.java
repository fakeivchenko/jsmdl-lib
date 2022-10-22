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

package com.ivchenko.jsmdl.servicehandler;

import com.google.api.client.http.HttpRequestFactory;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.VideoResponse;
import com.ivchenko.jsmdl.video.downloader.VideoDownloader;

public abstract class ServiceHandler<RES extends VideoResponse<?>> {
    protected final HttpRequestFactory requestFactory;

    /**
     * @param requestFactory Request factory to generate http requests.<br>
     *                       Can be nested from JsmdlConfig or created by the user if used standalone.
     */
    public ServiceHandler(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * @param videoRequest request to execute
     * @return response of given request
     */
    public abstract RES execute(VideoRequest videoRequest);


    /**
     * @return String array of request hosts supported by this handler
     */
    public abstract String[] getSupportedRequestHosts();

    public abstract VideoDownloader getVideoDownloader();


    /**
     * @return true if this object class is the same as the obj argument class; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }
}
