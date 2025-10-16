package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.TokenResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.Map;

public interface AuthService {
    User updateRole(RoleUpdateRequest roleUpdateRequest);

    TokenResponse reissueToken(Map<String, Object> claims);
}
