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
