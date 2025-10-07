package com.sprint.mission.discodeit.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final SessionRegistry sessionRegistry;

    @EventListener
    public void onAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        Object principal = auth.getPrincipal();

        // 현재 HTTP 세션 id 가져오기
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            log.warn("RequestAttributes is null while handling InteractiveAuthenticationSuccessEvent");
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }

        if (principal instanceof DiscodeitUserDetails details) {
            sessionRegistry.registerNewSession(session.getId(), details);
            log.debug("Registered session from InteractiveAuthenticationSuccessEvent: sessionId={}, user={}",
                    session.getId(), details.getUsername());
        }
    }
}
