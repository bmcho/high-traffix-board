package com.bmcho.hightrafficboard.controller.device;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.device.dto.WriteDeviceRequest;
import com.bmcho.hightrafficboard.entity.pojo.Device;
import com.bmcho.hightrafficboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final UserService userService;

    @GetMapping("")
    public BoardApiResponse<List<Device>> getDevices() {
        return BoardApiResponse.ok(userService.getDevices());
    }

    @PostMapping("")
    public BoardApiResponse<Device> addDevice(@RequestBody WriteDeviceRequest dto) {
        return BoardApiResponse.ok(userService.addDevice(dto));
    }
}
