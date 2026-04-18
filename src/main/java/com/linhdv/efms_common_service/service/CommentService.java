package com.linhdv.efms_common_service.service;

import com.linhdv.efms_common_service.dto.comment.CommentRequest;
import com.linhdv.efms_common_service.dto.comment.CommentResponse;
import com.linhdv.efms_common_service.wrapper.PagedResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service quản lý Comment và liên kết polymorphic với các entity.
 */
public interface CommentService {

    /**
     * Tạo comment mới và liên kết với entity qua entity_links.
     *
     * @param request   DTO chứa nội dung comment và reference
     * @param companyId ID công ty từ JWT context
     * @param userId    ID người dùng từ JWT context
     * @return CommentResponse
     */
    CommentResponse createComment(CommentRequest request, UUID companyId, UUID userId);

    /**
     * Lấy danh sách tất cả comments của một entity (không phân trang).
     *
     * @param referenceId   UUID của entity cha
     * @param referenceType Loại entity cha (ví dụ: "invoice")
     * @param companyId     ID công ty từ JWT context
     * @return Danh sách CommentResponse
     */
    List<CommentResponse> getCommentsByReference(UUID referenceId, String referenceType, UUID companyId);

    /**
     * Lấy danh sách comments của một entity với phân trang.
     *
     * @param referenceId   UUID của entity cha
     * @param referenceType Loại entity cha
     * @param companyId     ID công ty từ JWT context
     * @param page          Số trang (0-indexed)
     * @param size          Kích thước trang
     * @return PagedResponse<CommentResponse>
     */
    PagedResponse<CommentResponse> getPagedCommentsByReference(
            UUID referenceId, String referenceType, UUID companyId, int page, int size
    );

    /**
     * Lấy thông tin chi tiết một comment theo id.
     *
     * @param commentId ID của comment
     * @param companyId ID công ty từ JWT context
     * @return CommentResponse
     */
    CommentResponse getCommentById(UUID commentId, UUID companyId);

    /**
     * Xoá comment và tất cả entity_links liên quan.
     *
     * @param commentId ID của comment
     * @param companyId ID công ty từ JWT context
     */
    void deleteComment(UUID commentId, UUID companyId);
}
