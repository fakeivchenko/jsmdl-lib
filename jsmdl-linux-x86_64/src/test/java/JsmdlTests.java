import com.ivchenko.jsmdl.Jsmdl;
import com.ivchenko.jsmdl.request.BasicVideoRequest;
import com.ivchenko.jsmdl.request.VideoRequest;
import com.ivchenko.jsmdl.response.VideoResponse;
import com.ivchenko.jsmdl.video.Video;
import org.junit.Test;

public class JsmdlTests {
    @Test
    public void JsmdlWithConfig() {
        Jsmdl jsmdl = new Jsmdl();
        VideoRequest videoRequest = new BasicVideoRequest("https://twitter.com/PlayStation/status/1582356303468445698");
        VideoResponse<?> videoResponse = jsmdl.getVideoResponse(videoRequest);
        for (Video v : videoResponse.getAllVideos()) {
            jsmdl.downloadVideoToFile(v, v.getResolution() + ".mp4");
        }
    }
}
