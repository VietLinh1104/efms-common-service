package com.linhdv.efms_common_service.service.integration;

import com.linhdv.efms_common_service.dto.common.ApiResponse;
import com.linhdv.efms_common_service.dto.integration.UserBasicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityServiceClient {

    private final WebClient webClient;

    @Value("${efms.integration.identity-url}")
    private String identityUrl;

    /**
     * Batch fetch user info by IDs.
     * Expects endpoint POST /internal/users/batch to return ApiResponse<List<UserBasicInfo>>
     */
    public Map<UUID, UserBasicInfo> getBatchUsers(Set<UUID> userIds, UUID companyId) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            log.debug("Batch fetching {} users from Identity Service", userIds.size());
            
            // Note: The endpoint and contract must be implemented in Identity Service
            ApiResponse<List<UserBasicInfo>> response = webClient.post()
                    .uri(identityUrl + "/internal/users/batch")
                    .header("X-Company-Id", companyId.toString())
                    .bodyValue(userIds)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UserBasicInfo>>>() {})
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .collect(Collectors.toMap(UserBasicInfo::getId, user -> user));
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi Identity Service lấy thông tin users: {}", e.getMessage());
        }

        return Collections.emptyMap();
    }
}
