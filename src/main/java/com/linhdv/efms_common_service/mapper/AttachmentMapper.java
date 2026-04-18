package com.linhdv.efms_common_service.mapper;

import com.linhdv.efms_common_service.dto.attachment.AttachmentResponse;
import com.linhdv.efms_common_service.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AttachmentMapper {

    AttachmentResponse toResponse(Attachment attachment);

    List<AttachmentResponse> toResponseList(List<Attachment> attachments);
}
