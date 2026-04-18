package com.linhdv.efms_common_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "entity_links", schema = "common", uniqueConstraints = {@UniqueConstraint(name = "entity_links_reference_id_reference_type_item_id_item_type_key",
        columnNames = {
                "reference_id",
                "reference_type",
                "item_id",
                "item_type"})})
public class EntityLink {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Size(max = 50)
    @NotNull
    @Column(name = "reference_type", nullable = false, length = 50)
    private String referenceType;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Size(max = 50)
    @NotNull
    @Column(name = "item_type", nullable = false, length = 50)
    private String itemType;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;


}