package com.ivchenko.jsmdl.servicehandler;

import com.google.api.client.http.*;
import com.google.api.client.util.Lists;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.TwitterVideoResponse;
import com.ivchenko.jsmdl.video.TwitterVideo;
import com.ivchenko.jsmdl.video.downloader.TwitterVideoDownloader;
import com.ivchenko.jsmdl.video.downloader.VideoDownloader;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.Resolution;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.PlaylistParserException;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterServiceHandler extends ServiceHandler<TwitterVideoResponse> {
    private static final String[] SUPPORTED_REQUEST_HOSTS = new String[] {
            "twitter.com",
            "www.twitter.com"
    };

    private static final String VIDEO_PLAYER_PREFIX = "https://twitter.com/i/videos/tweet/";
    private static final String GUEST_TOKEN_API = "https://api.twitter.com/1.1/guest/activate.json";
    private static final String VIDEO_API = "https://api.twitter.com/1.1/videos/tweet/config/";

    private static final Pattern JS_URL_PATTERN = Pattern.compile("src=\"(.*js)");
    private static final Pattern BEARER_TOKEN_PATTERN = Pattern.compile("Bearer ([a-zA-Z0-9%])+");

    private static final Gson GSON = new Gson();

    private final TwitterVideoDownloader videoDownloader;

    public TwitterServiceHandler(HttpRequestFactory requestFactory) {
        super(requestFactory);
        this.videoDownloader = new TwitterVideoDownloader(requestFactory);
    }

    @Override
    public TwitterVideoResponse execute(VideoRequest videoRequest) {
        String videoId = getVideoId(videoRequest.getVideoUrl());
        String bearerToken = getBearerToken(videoId);
        String guestToken = getGuestToken(bearerToken);
        JsonObject videoApiResponse = getVideoApiResponse(videoId, bearerToken, guestToken);
        String thumbnailUrl = getThumbnailUrl(videoApiResponse);
        List<TwitterVideo> videos = getVideos(videoApiResponse, videoRequest.getVideoUrl(), thumbnailUrl);
        return new TwitterVideoResponse(videos);
    }

    private String getVideoId(String videoUrl) {
        List<String> pathParts = new GenericUrl(videoUrl).getPathParts();
        for (String part : pathParts) {
            if (part.matches("^\\d{19}$"))
                return part;
        }
        // TODO: 10/20/22 Throw exception
        return null;
    }

    private String getBearerToken(String videoId) {
        HttpRequest request;
        Matcher matcher;
        try {
            // Get twitter player response
            request = requestFactory.buildGetRequest(new GenericUrl(VIDEO_PLAYER_PREFIX + videoId));
            HttpResponse playerResponse = request.execute();
            Preconditions.checkState(playerResponse.isSuccessStatusCode());
            String playerResponseBody = playerResponse.parseAsString();
            // Find js url in Twitter player response body
            matcher = JS_URL_PATTERN.matcher(playerResponseBody);
            Preconditions.checkState(matcher.find());
            String jsUrl = matcher.group(1);
            // Get js response
            request = requestFactory.buildGetRequest(new GenericUrl(jsUrl));
            HttpResponse jsResponse = request.execute();
            Preconditions.checkState(playerResponse.isSuccessStatusCode());
            String jsResponseBody = jsResponse.parseAsString();
            // Find Bearer token in js response body
            matcher = BEARER_TOKEN_PATTERN.matcher(jsResponseBody);
            Preconditions.checkState(matcher.find());
            return matcher.group(0);
        } catch (IOException e) {
            // TODO: 10/20/22 Wrap exception
            throw new IllegalStateException();
        }
    }

    private String getGuestToken(String bearerToken) {
        // Creating HTTP headers with found bearer token
        HttpHeaders headers = new HttpHeaders().setAuthorization(bearerToken);
        try {
            // Get json with guest token
            HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(GUEST_TOKEN_API), null)
                    .setHeaders(headers);
            HttpResponse response = request.execute();
            Preconditions.checkState(response.isSuccessStatusCode());
            String responseBody = response.parseAsString();
            // Parsing json
            return GSON.fromJson(responseBody, JsonObject.class)
                    .get("guest_token")
                    .getAsString();
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new IllegalStateException();
        }
    }

    private JsonObject getVideoApiResponse(String videoId, String bearerToken, String guestToken) {
        // Creating HTTP headers with found bearer and guest tokens
        HttpHeaders headers = new HttpHeaders()
                .setAuthorization(bearerToken)
                .set("x-guest-token", guestToken);
        try {
            // Get response
            HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(VIDEO_API + videoId))
                    .setHeaders(headers);
            HttpResponse response = request.execute();
            Preconditions.checkState(response.isSuccessStatusCode());
            String responseBody = response.parseAsString();
            return GSON.fromJson(responseBody, JsonObject.class);
            // Parse body as JsonObject
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new IllegalStateException();
        }
    }

    private String getThumbnailUrl(JsonObject videoApiResponse) {
        // TODO: 10/21/22 Check if necessary
        String posterImageKey = "posterImage";
        if (videoApiResponse.has(posterImageKey)) {
            return videoApiResponse.get(posterImageKey).getAsString();
        } else return null;
    }

    private List<TwitterVideo> getVideos(JsonObject videoApiResponse, String originalUrl, String thumbnailUrl) {
        List<TwitterVideo> videos = Lists.newArrayList();

        // Initialize root playlist GenericUrl
        GenericUrl rootPlaylistUrl = new GenericUrl(
                videoApiResponse.get("track")
                        .getAsJsonObject()
                        .get("playbackUrl")
                        .getAsString()
        );

        String rootPlaylistBody;
        try {
            // Get root playlist as String
            HttpRequest request = requestFactory.buildGetRequest(rootPlaylistUrl);
            HttpResponse response = request.execute();
            Preconditions.checkState(response.isSuccessStatusCode());
            rootPlaylistBody = response.parseAsString();
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new IllegalStateException();
        }

        try {
            // Parse root playlist
            MasterPlaylist rootPlaylist = new MasterPlaylistParser().readPlaylist(rootPlaylistBody);
            rootPlaylist.variants().forEach(v -> {
                Resolution resolution = v.resolution().orElse(null);
                Preconditions.checkNotNull(resolution, "Cannot get resolution for video: " + v);
                videos.add(new TwitterVideo(
                        new Dimension(resolution.width(), resolution.height()),
                        originalUrl,
                        "https://" + rootPlaylistUrl.getHost() + v.uri(),
                        thumbnailUrl
                ));
            });
        } catch (PlaylistParserException e) {
            // TODO: 10/21/22 Wrap exception
            throw new IllegalStateException();
        }

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
