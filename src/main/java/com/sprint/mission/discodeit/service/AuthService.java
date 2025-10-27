package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;

public interface AuthService {

    UserResponse updateRole(RoleUpdateRequest roleUpdateRequest);

    void registerAdmin();

    JwtInformation refreshToken(String refreshToken);
}
