package com.bmcho.hightrafficboard.entity.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Device {

    private String deviceName;
    private String token;
}
