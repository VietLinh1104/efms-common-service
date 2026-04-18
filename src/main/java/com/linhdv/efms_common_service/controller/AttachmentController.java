package com.linhdv.efms_common_service.controller;

import com.linhdv.efms_common_service.dto.attachment.AttachmentRequest;
import com.linhdv.efms_common_service.dto.attachment.AttachmentResponse;
import com.linhdv.efms_common_service.dto.common.ApiResponse;
import com.linhdv.efms_common_service.service.AttachmentService;
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
@RequestMapping("/api/common/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment", description = "API quản lý file đính kèm chung cho toàn hệ thống")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    @Operation(summary = "Tải lên/Tạo mới một file đính kèm", description = "Tạo attachment và tự động liên kết với entity qua bảng entity_links.")
    public ResponseEntity<ApiResponse<AttachmentResponse>> createAttachment(
            @Parameter(description = "ID công ty (lấy từ Header hoặc JWT)", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @Parameter(description = "ID người dùng (lấy từ Header hoặc JWT)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody AttachmentRequest request) {
        
        log.info("Request create attachment from userId={} companyId={}", userId, companyId);
        AttachmentResponse response = attachmentService.createAttachment(request, companyId, userId);
        return ResponseEntity.ok(ApiResponse.success("Tạo file đính kèm thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết một file đính kèm")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachmentById(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        
        AttachmentResponse response = attachmentService.getAttachmentById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reference/{referenceType}/{referenceId}")
    @Operation(summary = "Lấy danh sách file đính kèm của một entity cụ thể")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachmentsByReference(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable String referenceType,
            @PathVariable UUID referenceId) {
        
        List<AttachmentResponse> responses = attachmentService.getAttachmentsByReference(referenceId, referenceType, companyId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả file đính kèm của công ty (có phân trang)")
    public ResponseEntity<ApiResponse<PagedResponse<AttachmentResponse>>> getAllAttachments(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PagedResponse<AttachmentResponse> pagedResponse = attachmentService.getAllAttachments(companyId, page, size);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá file đính kèm và huỷ liên kết")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @Parameter(description = "ID công ty", required = true)
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        
        attachmentService.deleteAttachment(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Xoá file đính kèm thành công"));
    }
}
