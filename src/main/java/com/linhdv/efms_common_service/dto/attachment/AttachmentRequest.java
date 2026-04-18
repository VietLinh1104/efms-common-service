package com.linhdv.efms_common_service.dto.attachment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request body để upload một attachment và liên kết với một entity")
public class AttachmentRequest {

    @NotNull(message = "referenceId là bắt buộc")
    @Schema(description = "UUID của entity cần gắn attachment (ví dụ: invoice id)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID referenceId;

    @NotBlank(message = "referenceType là bắt buộc")
    @Schema(description = "Kiểu entity (ví dụ: 'invoice', 'payment')", example = "invoice")
    private String referenceType;

    @NotBlank(message = "fileName là bắt buộc")
    @Schema(description = "Tên file gốc", example = "invoice_001.pdf")
    private String fileName;

    @Schema(description = "MIME type của file", example = "application/pdf")
    private String fileType;

    @Schema(description = "Kích thước file theo byte", example = "204800")
    private Long fileSize;

    @NotBlank(message = "fileUrl là bắt buộc")
    @Schema(description = "URL trỏ đến file đã upload lên storage", example = "https://storage.example.com/files/invoice_001.pdf")
    private String fileUrl;
}
