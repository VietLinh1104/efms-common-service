package com.linhdv.efms_common_service.repository;

import com.linhdv.efms_common_service.entity.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    /**
     * Tìm tất cả attachments thuộc một company (multi-tenancy).
     */
    Page<Attachment> findAllByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Tìm attachment theo id và company_id (đảm bảo multi-tenancy).
     */
    Optional<Attachment> findByIdAndCompanyId(UUID id, UUID companyId);

    /**
     * Lấy tất cả attachments liên kết tới một entity (reference_id + reference_type)
     * thông qua bảng entity_links, đồng thời kiểm tra company_id.
     */
    @Query("""
            SELECT a FROM Attachment a
            WHERE a.companyId = :companyId
              AND a.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'attachment'
              )
            ORDER BY a.createdAt DESC
            """)
    List<Attachment> findAllByReference(
            @Param("companyId") UUID companyId,
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType
    );

    /**
     * Đếm số attachments thuộc một reference.
     */
    @Query("""
            SELECT COUNT(a) FROM Attachment a
            WHERE a.companyId = :companyId
              AND a.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'attachment'
              )
            """)
    long countByReference(
            @Param("companyId") UUID companyId,
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType
    );

    /**
     * Kiểm tra attachment có tồn tại trong company hay không.
     */
    boolean existsByIdAndCompanyId(UUID id, UUID companyId);
}
