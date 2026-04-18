package com.linhdv.efms_common_service.service;

import com.linhdv.efms_common_service.entity.AuditLog;

import java.util.Map;
import java.util.UUID;

/**
 * Service ghi nhận lịch sử thay đổi dữ liệu vào bảng audit_logs.
 */
public interface AuditLogService {

    /**
     * Ghi log khi tạo mới một bản ghi.
     *
     * @param tableName Tên bảng (ví dụ: "attachments", "comments")
     * @param recordId  UUID của bản ghi mới
     * @param newData   Dữ liệu mới dưới dạng Map
     * @param changedBy UUID người thực hiện
     */
    void logCreate(String tableName, UUID recordId, Map<String, Object> newData, UUID changedBy);

    /**
     * Ghi log khi xoá một bản ghi.
     *
     * @param tableName Tên bảng
     * @param recordId  UUID bản ghi bị xoá
     * @param oldData   Dữ liệu cũ
     * @param changedBy UUID người thực hiện
     */
    void logDelete(String tableName, UUID recordId, Map<String, Object> oldData, UUID changedBy);

    /**
     * Ghi log khi cập nhật một bản ghi.
     *
     * @param tableName Tên bảng
     * @param recordId  UUID của bản ghi
     * @param oldData   Dữ liệu trước khi thay đổi
     * @param newData   Dữ liệu sau khi thay đổi
     * @param changedBy UUID người thực hiện
     */
    void logUpdate(String tableName, UUID recordId, Map<String, Object> oldData, Map<String, Object> newData, UUID changedBy);

    /**
     * Lấy lịch sử audit của một bản ghi cụ thể.
     *
     * @param tableName Tên bảng
     * @param recordId  UUID của bản ghi
     * @return Danh sách AuditLog (mới nhất trước)
     */
    java.util.List<AuditLog> getAuditHistory(String tableName, UUID recordId);
}
