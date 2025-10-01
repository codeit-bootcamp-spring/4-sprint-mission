package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {
    ResponseEntity getCsrfToken(CsrfToken csrfToken);

    ResponseEntity getUserDto(@AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails);

    ResponseEntity updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest);
}
