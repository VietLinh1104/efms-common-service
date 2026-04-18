package com.linhdv.efms_common_service.service.impl;

import com.linhdv.efms_common_service.dto.attachment.AttachmentRequest;
import com.linhdv.efms_common_service.dto.attachment.AttachmentResponse;
import com.linhdv.efms_common_service.entity.Attachment;
import com.linhdv.efms_common_service.entity.EntityLink;
import com.linhdv.efms_common_service.exception.DuplicateLinkException;
import com.linhdv.efms_common_service.exception.ResourceNotFoundException;
import com.linhdv.efms_common_service.mapper.AttachmentMapper;
import com.linhdv.efms_common_service.repository.AttachmentRepository;
import com.linhdv.efms_common_service.repository.EntityLinkRepository;
import com.linhdv.efms_common_service.service.AttachmentService;
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
public class AttachmentServiceImpl implements AttachmentService {

    private static final String ITEM_TYPE = "attachment";

    private final AttachmentRepository attachmentRepository;
    private final EntityLinkRepository entityLinkRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    @Transactional
    public AttachmentResponse createAttachment(AttachmentRequest request, UUID companyId, UUID userId) {
        log.info("Tạo attachment '{}' cho referenceType='{}', referenceId='{}', companyId='{}'",
                request.getFileName(), request.getReferenceType(), request.getReferenceId(), companyId);

        // 1. Lưu Attachment entity
        Attachment attachment = new Attachment();
        attachment.setId(UUID.randomUUID());
        attachment.setCompanyId(companyId);
        attachment.setFileName(request.getFileName());
        attachment.setFileType(request.getFileType());
        attachment.setFileSize(request.getFileSize());
        attachment.setFileUrl(request.getFileUrl());
        attachment.setCreatedBy(userId);
        attachment.setCreatedAt(Instant.now());

        Attachment saved = attachmentRepository.save(attachment);

        // 2. Tạo EntityLink để liên kết polymorphic
        createEntityLink(request.getReferenceId(), request.getReferenceType(), saved.getId(), ITEM_TYPE);

        log.info("Attachment '{}' đã được tạo thành công với id='{}'", saved.getFileName(), saved.getId());
        return attachmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByReference(UUID referenceId, String referenceType, UUID companyId) {
        log.debug("Lấy attachments cho referenceType='{}', referenceId='{}', companyId='{}'",
                referenceType, referenceId, companyId);

        List<Attachment> attachments = attachmentRepository.findAllByReference(companyId, referenceId, referenceType);
        return attachmentMapper.toResponseList(attachments);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(UUID attachmentId, UUID companyId) {
        Attachment attachment = attachmentRepository.findByIdAndCompanyId(attachmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));
        return attachmentMapper.toResponse(attachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(UUID attachmentId, UUID companyId) {
        log.info("Xoá attachment id='{}', companyId='{}'", attachmentId, companyId);

        Attachment attachment = attachmentRepository.findByIdAndCompanyId(attachmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        // Xoá tất cả entity_links liên quan trước
        entityLinkRepository.deleteAllByItemIdAndItemType(attachment.getId(), ITEM_TYPE);

        // Sau đó xoá attachment
        attachmentRepository.delete(attachment);

        log.info("Attachment '{}' đã được xoá thành công.", attachment.getFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AttachmentResponse> getAllAttachments(UUID companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Attachment> pageResult = attachmentRepository.findAllByCompanyId(companyId, pageable);

        List<AttachmentResponse> content = attachmentMapper.toResponseList(pageResult.getContent());
        return PagedResponse.of(content, page, size, pageResult.getTotalElements());
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
}
