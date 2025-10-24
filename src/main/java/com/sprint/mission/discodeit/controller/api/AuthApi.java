package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {
    ResponseEntity getCsrfToken(CsrfToken csrfToken);

    ResponseEntity reissueAccessToken(@CookieValue("REFRESH_TOKEN") String refreshToken, HttpServletResponse response);

    ResponseEntity updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest);
}
