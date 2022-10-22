import com.ivchenko.jsmdl.Jsmdl;
import com.ivchenko.jsmdl.request.BasicVideoRequest;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.VideoResponse;
import com.ivchenko.jsmdl.video.Video;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsmdlTests {
    static Jsmdl jsmdl;

    @BeforeClass
    public static void beforeClass() {
        jsmdl = new Jsmdl();
    }
    @Test
    public void JsmdlWithConfig() {
        VideoRequest videoRequest = new BasicVideoRequest("https://twitter.com/PlayStation/status/1582356303468445698");
        VideoResponse<?> videoResponse = jsmdl.getVideoResponse(videoRequest);
        for (Video v : videoResponse.getAllVideos()) {
            jsmdl.downloadVideoToFile(v, v.getResolution() + ".mp4");
        }
    }

    @Test
    public void simpleTikTokVideoRequest() {
        VideoRequest videoRequest =
                new BasicVideoRequest("https://www.tiktok.com/@cristopherisrael_actor/video/7146202207426219270?is_from_webapp=1&sender_device=pc");
        VideoResponse<?> videoResponse = jsmdl.getVideoResponse(videoRequest);
        for (Video v : videoResponse.getAllVideos()) {
            jsmdl.downloadVideoToFile(v, v.getResolution() + ".mp4");
        }
    }
}
