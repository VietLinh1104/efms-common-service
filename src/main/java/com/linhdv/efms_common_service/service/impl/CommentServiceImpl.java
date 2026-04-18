package com.linhdv.efms_common_service.service.impl;

import com.linhdv.efms_common_service.dto.comment.CommentRequest;
import com.linhdv.efms_common_service.dto.comment.CommentResponse;
import com.linhdv.efms_common_service.entity.Comment;
import com.linhdv.efms_common_service.entity.EntityLink;
import com.linhdv.efms_common_service.exception.DuplicateLinkException;
import com.linhdv.efms_common_service.exception.ResourceNotFoundException;
import com.linhdv.efms_common_service.mapper.CommentMapper;
import com.linhdv.efms_common_service.repository.CommentRepository;
import com.linhdv.efms_common_service.repository.EntityLinkRepository;
import com.linhdv.efms_common_service.service.CommentService;
import com.linhdv.efms_common_service.wrapper.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String ITEM_TYPE = "comment";

    private final CommentRepository commentRepository;
    private final EntityLinkRepository entityLinkRepository;
    private final CommentMapper commentMapper;
    private final com.linhdv.efms_common_service.service.integration.IdentityServiceClient identityServiceClient;

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, UUID companyId, UUID userId) {
        log.info("Tạo comment cho referenceType='{}', referenceId='{}', companyId='{}'",
                request.getReferenceType(), request.getReferenceId(), companyId);

        // 1. Lưu Comment entity
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setCompanyId(companyId);
        comment.setContent(request.getContent());
        comment.setAuthorId(userId);
        comment.setCreatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);

        // 2. Tạo EntityLink để liên kết polymorphic
        createEntityLink(request.getReferenceId(), request.getReferenceType(), saved.getId(), ITEM_TYPE);

        log.info("Comment '{}' đã được tạo thành công.", saved.getId());
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByReference(UUID referenceId, String referenceType, UUID companyId) {
        log.debug("Lấy comments cho referenceType='{}', referenceId='{}', companyId='{}'",
                referenceType, referenceId, companyId);

        List<Comment> comments = commentRepository.findAllByReference(companyId, referenceId, referenceType);
        List<CommentResponse> responses = commentMapper.toResponseList(comments);
        enrichCommentAuthorInfo(responses, companyId);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getPagedCommentsByReference(
            UUID referenceId, String referenceType, UUID companyId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> pageResult = commentRepository.findPagedByReference(companyId, referenceId, referenceType, pageable);

        List<CommentResponse> content = commentMapper.toResponseList(pageResult.getContent());
        enrichCommentAuthorInfo(content, companyId);
        return PagedResponse.of(content, page, size, pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID commentId, UUID companyId) {
        Comment comment = commentRepository.findByIdAndCompanyId(commentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, UUID companyId) {
        log.info("Xoá comment id='{}', companyId='{}'", commentId, companyId);

        Comment comment = commentRepository.findByIdAndCompanyId(commentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        // Xoá tất cả entity_links liên quan trước
        entityLinkRepository.deleteAllByItemIdAndItemType(comment.getId(), ITEM_TYPE);

        // Sau đó xoá comment
        commentRepository.delete(comment);

        log.info("Comment '{}' đã được xoá thành công.", commentId);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private void createEntityLink(UUID referenceId, String referenceType, UUID itemId, String itemType) {
        boolean exists = entityLinkRepository.existsByReferenceIdAndReferenceTypeAndItemIdAndItemType(
                referenceId, referenceType, itemId, itemType
        );
        if (exists) {
            throw new DuplicateLinkException(
                    String.format("EntityLink đã tồn tại: referenceId=%s, referenceType=%s, itemId=%s, itemType=%s",
                            referenceId, referenceType, itemId, itemType)
            );
        }

        EntityLink link = new EntityLink();
        link.setId(UUID.randomUUID());
        link.setReferenceId(referenceId);
        link.setReferenceType(referenceType);
        link.setItemId(itemId);
        link.setItemType(itemType);
        link.setCreatedAt(Instant.now());

        entityLinkRepository.save(link);
    }

    private void enrichCommentAuthorInfo(List<CommentResponse> responses, UUID companyId) {
        if (responses == null || responses.isEmpty()) return;

        java.util.Set<UUID> authorIds = responses.stream()
                .map(CommentResponse::getAuthorId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        java.util.Map<UUID, com.linhdv.efms_common_service.dto.integration.UserBasicInfo> userMap =
                identityServiceClient.getBatchUsers(authorIds, companyId);

        for (CommentResponse response : responses) {
            com.linhdv.efms_common_service.dto.integration.UserBasicInfo userInfo = userMap.get(response.getAuthorId());
            if (userInfo != null) {
                response.setAuthorName(userInfo.getFullName());
                response.setAuthorAvatar(userInfo.getAvatar());
            }
        }
    }
}
