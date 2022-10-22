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
import com.ivchenko.jsmdl.video.TwitterVideo;
import com.ivchenko.jsmdl.video.Video;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.*;

public class TwitterVideoDownloader extends BasicVideoDownloader {
    public TwitterVideoDownloader(HttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    public Class<? extends Video> accepts() {
        return TwitterVideo.class;
    }

    @Override
    public InputStream getInputStream(Video video) {
        File tempFile;
        try {
            tempFile = File.createTempFile("jsmdl-", ".mp4");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        }

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video.getDownloadUrl());
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile, 0, 0)) {
            record(grabber, recorder);
            return new FileInputStream(tempFile);
        } catch (FrameGrabber.Exception e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        } catch (FrameRecorder.Exception e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            // Should not happen
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadToFile(Video video, File file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video.getDownloadUrl());
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file, 0, 0)) {
            record(grabber, recorder);
        } catch (FrameGrabber.Exception e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        } catch (FrameRecorder.Exception e) {
            // TODO: 10/21/22 Wrap exception
            throw new RuntimeException(e);
        }
    }

    private void record(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder)
            throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        grabber.start();
        recorder.setImageWidth(grabber.getImageWidth());
        recorder.setImageHeight(grabber.getImageHeight());
        recorder.setAudioChannels(grabber.getAudioChannels()); // No sound without this
        recorder.start(grabber.getFormatContext());

        for (AVPacket frame; (frame = grabber.grabPacket()) != null; ) {
            recorder.recordPacket(frame);
        }
    }
}
