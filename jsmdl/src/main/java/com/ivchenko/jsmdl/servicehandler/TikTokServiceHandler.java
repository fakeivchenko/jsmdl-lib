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

import com.google.api.client.http.*;
import com.google.api.client.util.Lists;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.TikTokVideoResponse;
import com.ivchenko.jsmdl.video.TikTokVideo;
import com.ivchenko.jsmdl.video.downloader.TikTokVideoDownloader;
import com.ivchenko.jsmdl.video.downloader.VideoDownloader;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TikTokServiceHandler extends ServiceHandler<TikTokVideoResponse> {
    private static final String[] SUPPORTED_REQUEST_HOSTS = new String[] {
            "tiktok.com",
            "www.tiktok.com",
            "vm.tiktok.com"
    };

    private static final String TIK_TOK_API = "https://api16-normal-c-useast1a.tiktokv.com";
    private static final HttpHeaders USER_AGENT_HTTP_HEADERS = new HttpHeaders() {{
       setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
    }};

    private static final String WEB_SHARE_URL_REGEX =
            "^((https://)|(http://))((www\\.)|())tiktok\\.com/@.+/video/\\d{19}?(\\?.+)?$";
    private static final String MOBILE_SHARE_URL_REGEX =
            "^((https://)|(http://))((www\\.)|())vm\\.tiktok\\.com/.+$";

    private static final Gson GSON = new Gson();

    private final TikTokVideoDownloader videoDownloader;

    public TikTokServiceHandler(HttpRequestFactory requestFactory) {
        super(requestFactory);
        this.videoDownloader = new TikTokVideoDownloader(requestFactory);
    }

    @Override
    public TikTokVideoResponse execute(VideoRequest videoRequest) {
        String videoId = getId(videoRequest.getVideoUrl());
        JsonObject apiResponse = getApiResponse(videoId);
        String thumbnailUrl = getThumbnailUrl(apiResponse);
        List<TikTokVideo> videos = getVideos(apiResponse, videoRequest.getVideoUrl(), thumbnailUrl);
        return new TikTokVideoResponse(videos);
    }

    private String getId(String videoUrl) {
        if (videoUrl.matches(MOBILE_SHARE_URL_REGEX))
            return getIdByMobileUrl(videoUrl);
        if (videoUrl.matches(WEB_SHARE_URL_REGEX))
            return getIdByWebUrl(videoUrl);
        throw new IllegalStateException(); // TODO: 10/22/22 change
    }

    private String getIdByMobileUrl(String videoUrl) {
        try {
            HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(videoUrl));
            HttpResponse response = request.execute();
            String location = response.getHeaders().getLocation();
            Preconditions.checkState(location != null && location.isBlank());
            return location;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: 10/22/22 Wrap exception
        }
    }

    private String getIdByWebUrl(String videoUrl) {
        List<String> pathParts = new GenericUrl(videoUrl).getPathParts();
        for (String s : pathParts)
            if (s.matches("^\\d{19}$"))
                return s;
        throw new IllegalStateException(); // TODO: 10/22/22 change
    }

    private JsonObject getApiResponse(String videoId) {
        try {
            String requestUrl =
                    TIK_TOK_API + "/aweme/v1/feed/?version_code=2613&device_type=Pixel5&aweme_id=" + videoId;
            HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(requestUrl))
                    .setHeaders(USER_AGENT_HTTP_HEADERS)
                    .setSuppressUserAgentSuffix(true);
            HttpResponse response = request.execute();
            Preconditions.checkState(response.isSuccessStatusCode());
            return GSON.fromJson(response.parseAsString(), JsonObject.class)
                    .getAsJsonArray("aweme_list")
                    .get(0)
                    .getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: 10/22/22 Wrap exception
        }
    }

    private String getThumbnailUrl(JsonObject apiResponse) {
        return apiResponse
                .getAsJsonObject("video")
                .getAsJsonObject("cover")
                .getAsJsonArray("url_list")
                .get(0)
                .getAsString();
    }

    private List<TikTokVideo> getVideos(JsonObject apiResponse, String originalUrl, String thumbnailUrl) {
        List<TikTokVideo> videos = Lists.newArrayList();

        JsonObject playAddrJson = apiResponse
                .getAsJsonObject("video")
                .getAsJsonObject("play_addr");
        Dimension resolution = new Dimension(
                playAddrJson.get("width").getAsInt(),
                playAddrJson.get("height").getAsInt()
        );

        JsonArray urlListJson = playAddrJson.getAsJsonArray("url_list");
        urlListJson.forEach(url -> videos.add(
                new TikTokVideo(resolution, originalUrl, url.getAsString(), thumbnailUrl)
        ));
        return videos;
    }

    @Override
    public String[] getSupportedRequestHosts() {
        return SUPPORTED_REQUEST_HOSTS;
    }

    @Override
    public VideoDownloader getVideoDownloader() {
        return videoDownloader;
    }
}
