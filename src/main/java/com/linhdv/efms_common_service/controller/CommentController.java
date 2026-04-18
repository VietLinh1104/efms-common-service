package com.linhdv.efms_common_service.controller;

import com.linhdv.efms_common_service.dto.comment.CommentRequest;
import com.linhdv.efms_common_service.dto.comment.CommentResponse;
import com.linhdv.efms_common_service.dto.common.ApiResponse;
import com.linhdv.efms_common_service.service.CommentService;
import com.linhdv.efms_common_service.wrapper.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/common/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "API quản lý bình luận chung cho toàn hệ thống")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Viết bình luận mới", description = "Tạo bình luận và tự động liên kết với entity qua bảng entity_links.")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Parameter(description = "ID công ty (lấy từ Header hoặc JWT)", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @Parameter(description = "ID người dùng (lấy từ Header hoặc JWT)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CommentRequest request) {

        log.info("Request create comment from userId={} companyId={}", userId, companyId);
        CommentResponse response = commentService.createComment(request, companyId, userId);
        return ResponseEntity.ok(ApiResponse.success("Tạo bình luận thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết một bình luận")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {

        CommentResponse response = commentService.getCommentById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reference/{referenceType}/{referenceId}")
    @Operation(summary = "Lấy danh sách bình luận của một entity (không phân trang)")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByReference(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable String referenceType,
            @PathVariable UUID referenceId) {

        List<CommentResponse> responses = commentService.getCommentsByReference(referenceId, referenceType, companyId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/reference/{referenceType}/{referenceId}/paged")
    @Operation(summary = "Lấy danh sách bình luận của một entity (có phân trang)")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getPagedCommentsByReference(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable String referenceType,
            @PathVariable UUID referenceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<CommentResponse> pagedResponse = commentService.getPagedCommentsByReference(referenceId, referenceType, companyId, page, size);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá bình luận và huỷ liên kết")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {

        commentService.deleteComment(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Xoá bình luận thành công"));
    }
}
