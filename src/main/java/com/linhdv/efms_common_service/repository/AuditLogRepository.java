package com.linhdv.efms_common_service.repository;

import com.linhdv.efms_common_service.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Lấy toàn bộ audit log theo record_id và table_name.
     */
    List<AuditLog> findAllByRecordIdAndTableNameOrderByChangedAtDesc(UUID recordId, String tableName);

    /**
     * Lấy audit log theo table_name với phân trang.
     */
    Page<AuditLog> findAllByTableNameOrderByChangedAtDesc(String tableName, Pageable pageable);

    /**
     * Lấy audit log theo người thực hiện.
     */
    Page<AuditLog> findAllByChangedByOrderByChangedAtDesc(UUID changedBy, Pageable pageable);

    /**
     * Đếm số thay đổi của một record.
     */
    long countByRecordIdAndTableName(UUID recordId, String tableName);
}
