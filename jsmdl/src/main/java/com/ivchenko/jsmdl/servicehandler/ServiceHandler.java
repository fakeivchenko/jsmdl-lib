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
