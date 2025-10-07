package com.sprint.mission.discodeit.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

// SPA 환경에서는 헤더 기반 CSRF 토큰 검증을 지원하고, 동시에 기존 방식(파라미터)도 fallback으로 지원
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         *
         * 항상 XorCsrfTokenRequestAttributeHandler를 사용해서 응답에 CSRF 토큰을 심어줌 -> BREACH 방지용
         * (BREACH: HTTPS 통신 시 HTTP 압축 취약점을 악용하여 공격자가 민감한 정보를 추출하는 공격 기법)
         */
        this.xor.handle(request, response, csrfToken);
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         *
         * csrfToken.get()을 호출해서 토큰을 강제로 로드 -> 이렇게 하면 쿠키(XSRF-TOKEN)에 토큰이 담김
         */
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         *
         * In all other cases (e.g. if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
         * when a server-side rendered form includes the _csrf request parameter as a
         * hidden input.
         *
         * 요청에 헤더(X-XSRF-TOKEN)가 있으면 -> 그냥 raw value 그대로 비교 (CsrfTokenRequestAttributeHandler)
         * 요청에 헤더가 없고 파라미터(_csrf)로 들어오면 -> Xor 처리된 값과 비교 (XorCsrfTokenRequestAttributeHandler)
         * 즉, SPA -> 헤더 우선 / 서버사이드 렌더링 폼 -> 파라미터 우선 이런 식으로 분기
         */
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
    }
}
