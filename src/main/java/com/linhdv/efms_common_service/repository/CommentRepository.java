package com.linhdv.efms_common_service.repository;

import com.linhdv.efms_common_service.entity.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Tìm tất cả comments thuộc một company (multi-tenancy).
     */
    Page<Comment> findAllByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Tìm comment theo id và company_id (đảm bảo multi-tenancy).
     */
    Optional<Comment> findByIdAndCompanyId(UUID id, UUID companyId);

    /**
     * Lấy tất cả comments liên kết tới một entity (reference_id + reference_type)
     * thông qua bảng entity_links, đồng thời kiểm tra company_id.
     */
    @Query("""
            SELECT c FROM Comment c
            WHERE c.companyId = :companyId
              AND c.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'comment'
              )
            ORDER BY c.createdAt ASC
            """)
    List<Comment> findAllByReference(
            @Param("companyId") UUID companyId,
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType
    );

    /**
     * Lấy danh sách comments theo reference với phân trang.
     */
    @Query(value = """
            SELECT c FROM Comment c
            WHERE c.companyId = :companyId
              AND c.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'comment'
              )
            ORDER BY c.createdAt ASC
            """,
            countQuery = """
            SELECT COUNT(c) FROM Comment c
            WHERE c.companyId = :companyId
              AND c.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'comment'
              )
            """)
    Page<Comment> findPagedByReference(
            @Param("companyId") UUID companyId,
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType,
            Pageable pageable
    );

    /**
     * Đếm số comments thuộc một reference.
     */
    @Query("""
            SELECT COUNT(c) FROM Comment c
            WHERE c.companyId = :companyId
              AND c.id IN (
                  SELECT el.itemId FROM EntityLink el
                  WHERE el.referenceId   = :referenceId
                    AND el.referenceType = :referenceType
                    AND el.itemType      = 'comment'
              )
            """)
    long countByReference(
            @Param("companyId") UUID companyId,
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType
    );

    /**
     * Kiểm tra comment có tồn tại trong company hay không.
     */
    boolean existsByIdAndCompanyId(UUID id, UUID companyId);
}
