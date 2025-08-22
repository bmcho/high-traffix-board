package com.bmcho.hightrafficboard.controller.comment;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.controller.comment.dto.CommentResponse;
import com.bmcho.hightrafficboard.controller.comment.dto.WriteCommentRequest;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/{boardId}/articles/{articleId}/comments")
    public BoardApiResponse<CommentResponse> writeComment(@PathVariable Long boardId,
                                                          @PathVariable Long articleId,
                                                          @RequestBody WriteCommentRequest dto) {

        CommentEntity commentEntity = commentService.writeComment(boardId, articleId, dto);
        CommentResponse response = CommentResponse.from(commentEntity);

        return BoardApiResponse.ok(response);
    }

    @PutMapping("/{boardId}/articles/{articleId}/comments/{commentId}")
    public BoardApiResponse<CommentResponse> writeComment(@PathVariable Long boardId,
                                                          @PathVariable Long articleId,
                                                          @PathVariable Long commentId,
                                                          @RequestBody WriteCommentRequest dto) {

        CommentEntity commentEntity = commentService.updateComment(boardId, articleId, commentId, dto);
        CommentResponse response = CommentResponse.from(commentEntity);

        return BoardApiResponse.ok(response);
    }

    @DeleteMapping("/{boardId}/articles/{articleId}/comments/{commentId}")
    public BoardApiResponse<String> writeComment(@PathVariable Long boardId,
                                               @PathVariable Long articleId,
                                               @PathVariable Long commentId) {
        commentService.deleteComment(boardId, articleId, commentId);
        return BoardApiResponse.ok("comment is deleted");
    }
}
