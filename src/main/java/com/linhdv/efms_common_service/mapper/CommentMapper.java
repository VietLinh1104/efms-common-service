package com.linhdv.efms_common_service.mapper;

import com.linhdv.efms_common_service.dto.comment.CommentResponse;
import com.linhdv.efms_common_service.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);
}
