package com.linhdv.efms_common_service.repository;

import com.linhdv.efms_common_service.entity.EntityLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityLinkRepository extends JpaRepository<EntityLink, UUID> {

    /**
     * Tìm EntityLink theo đủ 4 chiều: reference_id, reference_type, item_id, item_type.
     */
    Optional<EntityLink> findByReferenceIdAndReferenceTypeAndItemIdAndItemType(
            UUID referenceId,
            String referenceType,
            UUID itemId,
            String itemType
    );

    /**
     * Lấy tất cả liên kết của một item (attachment hoặc comment) bất kể reference.
     */
    List<EntityLink> findAllByItemIdAndItemType(UUID itemId, String itemType);

    /**
     * Lấy tất cả item_id của một loại item (attachment/comment) gắn với một reference.
     */
    @Query("""
            SELECT el.itemId FROM EntityLink el
            WHERE el.referenceId   = :referenceId
              AND el.referenceType = :referenceType
              AND el.itemType      = :itemType
            """)
    List<UUID> findItemIdsByReference(
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType,
            @Param("itemType") String itemType
    );

    /**
     * Kiểm tra xem một liên kết đã tồn tại chưa (tránh duplicate).
     */
    boolean existsByReferenceIdAndReferenceTypeAndItemIdAndItemType(
            UUID referenceId,
            String referenceType,
            UUID itemId,
            String itemType
    );

    /**
     * Xoá tất cả EntityLink của một item (khi xoá attachment/comment).
     */
    @Modifying
    @Query("""
            DELETE FROM EntityLink el
            WHERE el.itemId   = :itemId
              AND el.itemType = :itemType
            """)
    void deleteAllByItemIdAndItemType(
            @Param("itemId") UUID itemId,
            @Param("itemType") String itemType
    );

    /**
     * Xoá tất cả EntityLink thuộc về một reference (khi xoá entity cha).
     */
    @Modifying
    @Query("""
            DELETE FROM EntityLink el
            WHERE el.referenceId   = :referenceId
              AND el.referenceType = :referenceType
            """)
    void deleteAllByReference(
            @Param("referenceId") UUID referenceId,
            @Param("referenceType") String referenceType
    );
}
