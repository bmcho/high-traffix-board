package com.bmcho.hightrafficboard.controller.advertisement;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.advertisement.dto.AdvertisementResponse;
import com.bmcho.hightrafficboard.controller.advertisement.dto.WriteAdRequest;
import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import com.bmcho.hightrafficboard.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping("")
    public BoardApiResponse<AdvertisementResponse> writeAd(@RequestBody WriteAdRequest dto) {
        AdvertisementEntity entity = advertisementService.writeAd(dto);
        AdvertisementResponse response = AdvertisementResponse.from(entity);
        return BoardApiResponse.ok(response);
    }

    @GetMapping("")
    public BoardApiResponse<List<AdvertisementResponse>> getAdList() {
        List<AdvertisementEntity> entityList = advertisementService.getAdList();
        List<AdvertisementResponse> responseList = entityList.stream().map(AdvertisementResponse::from).toList();
        return BoardApiResponse.ok(responseList);
    }

    @GetMapping("/{adId}")
    public BoardApiResponse<AdvertisementResponse> getAdList(@PathVariable Long adId) {
        AdvertisementEntity entity = advertisementService.getAd(adId);
        AdvertisementResponse response = AdvertisementResponse.from(entity);
        return BoardApiResponse.ok(response);
    }
}