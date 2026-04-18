package com.linhdv.efms_common_service.service;

import com.linhdv.efms_common_service.dto.attachment.AttachmentRequest;
import com.linhdv.efms_common_service.dto.attachment.AttachmentResponse;
import com.linhdv.efms_common_service.wrapper.PagedResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service quản lý Attachment và liên kết polymorphic với các entity.
 */
public interface AttachmentService {

    /**
     * Tạo attachment mới và liên kết với entity qua entity_links.
     *
     * @param request   DTO chứa thông tin file và reference
     * @param companyId ID công ty từ JWT context
     * @param userId    ID người dùng từ JWT context
     * @return AttachmentResponse
     */
    AttachmentResponse createAttachment(AttachmentRequest request, UUID companyId, UUID userId);

    /**
     * Lấy danh sách tất cả attachments của một entity (không phân trang).
     *
     * @param referenceId   UUID của entity cha
     * @param referenceType Loại entity cha (ví dụ: "invoice")
     * @param companyId     ID công ty từ JWT context
     * @return Danh sách AttachmentResponse
     */
    List<AttachmentResponse> getAttachmentsByReference(UUID referenceId, String referenceType, UUID companyId);

    /**
     * Lấy thông tin chi tiết một attachment theo id.
     *
     * @param attachmentId ID của attachment
     * @param companyId    ID công ty từ JWT context
     * @return AttachmentResponse
     */
    AttachmentResponse getAttachmentById(UUID attachmentId, UUID companyId);

    /**
     * Xoá attachment và tất cả entity_links liên quan.
     *
     * @param attachmentId ID của attachment
     * @param companyId    ID công ty từ JWT context
     */
    void deleteAttachment(UUID attachmentId, UUID companyId);

    /**
     * Lấy danh sách tất cả attachments của công ty (có phân trang).
     *
     * @param companyId ID công ty từ JWT context
     * @param page      Số trang (0-indexed)
     * @param size      Kích thước trang
     * @return PagedResponse<AttachmentResponse>
     */
    PagedResponse<AttachmentResponse> getAllAttachments(UUID companyId, int page, int size);
}
