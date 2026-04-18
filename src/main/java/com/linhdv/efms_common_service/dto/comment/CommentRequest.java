package com.linhdv.efms_common_service.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request body để tạo một comment và liên kết với một entity")
public class CommentRequest {

    @NotNull(message = "referenceId là bắt buộc")
    @Schema(description = "UUID của entity cần gắn comment", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID referenceId;

    @NotBlank(message = "referenceType là bắt buộc")
    @Schema(description = "Kiểu entity (ví dụ: 'invoice', 'payment')", example = "invoice")
    private String referenceType;

    @NotBlank(message = "Nội dung comment không được để trống")
    @Schema(description = "Nội dung comment", example = "Hoá đơn này cần bổ sung chứng từ gốc.")
    private String content;
}
