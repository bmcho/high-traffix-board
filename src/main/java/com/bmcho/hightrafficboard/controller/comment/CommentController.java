package com.bmcho.hightrafficboard.controller.comment;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.comment.dto.CommentResponse;
import com.bmcho.hightrafficboard.controller.comment.dto.WriteCommentRequest;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/{boardId}/articles/{articleId}")
    public BoardApiResponse<CommentResponse> writeComment(@PathVariable Long boardId,
                                                          @PathVariable Long articleId,
                                                          @RequestBody WriteCommentRequest dto) {

        CommentEntity commentEntity = commentService.writeComment(boardId, articleId, dto);
        CommentResponse response = CommentResponse.from(commentEntity);

        return BoardApiResponse.ok(response);
    }
}
