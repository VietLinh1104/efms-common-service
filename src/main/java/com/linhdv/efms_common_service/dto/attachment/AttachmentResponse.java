package com.linhdv.efms_common_service.dto.attachment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Thông tin chi tiết của một attachment")
public class AttachmentResponse {

    @Schema(description = "ID của attachment")
    private UUID id;

    @Schema(description = "ID công ty sở hữu")
    private UUID companyId;

    @Schema(description = "Tên file gốc")
    private String fileName;

    @Schema(description = "MIME type")
    private String fileType;

    @Schema(description = "Kích thước file theo byte")
    private Long fileSize;

    @Schema(description = "URL truy cập file")
    private String fileUrl;

    @Schema(description = "ID người upload")
    private UUID createdBy;

    @Schema(description = "Tên người upload")
    private String createdByName;

    @Schema(description = "Thời điểm upload")
    private Instant createdAt;
}
