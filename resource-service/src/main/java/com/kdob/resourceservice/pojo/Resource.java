package com.kdob.resourceservice.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resource {

    private Long id;
    private String key;
    private byte[] resource;
}
