package com.bmcho.hightrafficboard.controller.advertisement;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.advertisement.dto.AdvertisementResponse;
import com.bmcho.hightrafficboard.controller.advertisement.dto.WriteAdRequest;
import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import com.bmcho.hightrafficboard.service.AdvertisementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping("/admin/ads")
    public BoardApiResponse<AdvertisementResponse> writeAd(@RequestBody WriteAdRequest dto) {
        AdvertisementEntity entity = advertisementService.writeAd(dto);
        AdvertisementResponse response = AdvertisementResponse.from(entity);
        return BoardApiResponse.ok(response);
    }

    @GetMapping("/ads")
    public BoardApiResponse<List<AdvertisementResponse>> getAdList() {
        List<AdvertisementEntity> entityList = advertisementService.getAdList();
        List<AdvertisementResponse> responseList = entityList.stream().map(AdvertisementResponse::from).toList();
        return BoardApiResponse.ok(responseList);
    }

    @GetMapping("/ads/{adId}")
    public BoardApiResponse<AdvertisementResponse> getAdList(@PathVariable Long adId, HttpServletRequest request, @RequestParam(required = false) Boolean isTrueView) {
        String ipAddress = request.getRemoteAddr();
        AdvertisementEntity entity = advertisementService.getAd(adId, ipAddress, isTrueView);
        AdvertisementResponse response = AdvertisementResponse.from(entity);
        return BoardApiResponse.ok(response);
    }

    @PostMapping("/ads/{adId}")
    public BoardApiResponse<String> clickAd(@PathVariable Long adId, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        advertisementService.clickAd(adId, ipAddress);
        return BoardApiResponse.ok("click");
    }
}