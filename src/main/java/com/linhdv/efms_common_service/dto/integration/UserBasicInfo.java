package com.linhdv.efms_common_service.dto.integration;

import lombok.Data;

import java.util.UUID;

@Data
public class UserBasicInfo {
    private UUID id;
    private String fullName;
    private String email;
    private String avatar;
}
