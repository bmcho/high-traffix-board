package com.bmcho.hightrafficboard.controller.notice;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.notice.dto.NoticeResponse;
import com.bmcho.hightrafficboard.controller.notice.dto.WriteNoticeRequest;
import com.bmcho.hightrafficboard.entity.NoticeEntity;
import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import com.bmcho.hightrafficboard.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("")
    public BoardApiResponse<NoticeResponse> writeNotice(@RequestBody WriteNoticeRequest dto) {
        NoticeEntity entity = noticeService.writeNotice(dto);
        NoticeResponse response = NoticeResponse.from(entity);
        return BoardApiResponse.ok(response);
    }

    @GetMapping("/{noticeId}")
    public BoardApiResponse<NoticeResponse> getNotice(@PathVariable Long noticeId) {
        NoticeEntity entity = noticeService.getNotice(noticeId);
        NoticeResponse response = NoticeResponse.from(entity);
        return BoardApiResponse.ok(response);
    }

}
