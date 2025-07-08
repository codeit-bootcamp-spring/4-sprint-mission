package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.LoginDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface LoginService { // 로그인서비스는 로그인만 해주면 되는것 아닌가..?

    LoginDto login(LoginRequest loginRequest);
}
