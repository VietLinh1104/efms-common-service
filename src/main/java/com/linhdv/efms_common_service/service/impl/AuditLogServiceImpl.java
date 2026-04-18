package com.linhdv.efms_common_service.service.impl;

import com.linhdv.efms_common_service.entity.AuditLog;
import com.linhdv.efms_common_service.repository.AuditLogRepository;
import com.linhdv.efms_common_service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(String tableName, UUID recordId, Map<String, Object> newData, UUID changedBy) {
        saveAuditLog(tableName, recordId, "CREATE", null, newData, changedBy);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDelete(String tableName, UUID recordId, Map<String, Object> oldData, UUID changedBy) {
        saveAuditLog(tableName, recordId, "DELETE", oldData, null, changedBy);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(String tableName, UUID recordId, Map<String, Object> oldData, Map<String, Object> newData, UUID changedBy) {
        saveAuditLog(tableName, recordId, "UPDATE", oldData, newData, changedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditHistory(String tableName, UUID recordId) {
        return auditLogRepository.findAllByRecordIdAndTableNameOrderByChangedAtDesc(recordId, tableName);
    }

    // ─── Private helper ───────────────────────────────────────────────────────

    private void saveAuditLog(
            String tableName,
            UUID recordId,
            String action,
            Map<String, Object> oldData,
            Map<String, Object> newData,
            UUID changedBy
    ) {
        try {
            AuditLog log = new AuditLog();
            log.setTableName(tableName);
            log.setRecordId(recordId);
            log.setAction(action);
            log.setOldData(oldData);
            log.setNewData(newData);
            log.setChangedBy(changedBy);
            log.setChangedAt(Instant.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Không thể ghi audit log cho bảng '{}', recordId='{}': {}", tableName, recordId, e.getMessage());
        }
    }
}
