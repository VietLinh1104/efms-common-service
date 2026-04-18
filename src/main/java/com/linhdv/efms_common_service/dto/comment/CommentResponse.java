package com.linhdv.efms_common_service.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Thông tin chi tiết của một comment")
public class CommentResponse {

    @Schema(description = "ID của comment")
    private UUID id;

    @Schema(description = "ID công ty sở hữu")
    private UUID companyId;

    @Schema(description = "Nội dung comment")
    private String content;

    @Schema(description = "ID tác giả comment")
    private UUID authorId;

    @Schema(description = "Thời điểm tạo")
    private Instant createdAt;
}
