package com.ivchenko.jsmdl.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BasicVideoRequest extends VideoRequest {
    public BasicVideoRequest(String videoUrl) {
        super(videoUrl);
    }
}
