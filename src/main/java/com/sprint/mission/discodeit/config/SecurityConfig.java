package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity(debug = true )
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final DiscodeitUserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain filterChain (HttpSecurity http, SessionRegistry sessionRegistry, DataSource dataSource) throws Exception {

    http
        .csrf(csrf -> csrf.
            csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )

        .formLogin(login -> login
            .loginPage("/index.html")
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )

        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .deleteCookies("JSESSIONID"))

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/",
                "/index.html",
                "/assets/**",
                "/css/**",
                "/js/**",
                "/api/auth/**",
                "/api/users"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request,
                response,
                authException) -> { response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write("인증이 필요합니다.");
            })
            .accessDeniedHandler((request,
                response,
                accessDeniedException) ->  { response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("권한이 없습니다.");
            }))

        .sessionManagement(session ->session.
            sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry))
            )

        .rememberMe(remember -> remember
            .key("uniqueAndSecret")
            .tokenRepository(persistentTokenRepository(dataSource))
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(3600)
            .userDetailsService(userDetailsService)
        )

        ;

    return http.build();
  }

  static class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        Supplier<CsrfToken> csrfToken) {

      this.xor.handle(request,response,csrfToken);

    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
      String headerValue = request.getHeader(csrfToken.getHeaderName());

      return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
    }
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    //계층 구조 정의
    String hierarchy = "ROLE_ADMIN > ROLE_CHANNEL_MANAGER \n ROLE_CHANNEL_MANAGER > ROLE_USER";
    roleHierarchy.setHierarchy(hierarchy);
    return roleHierarchy;
  }
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() { // 세션 등록/조회용 레지스트리
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
  }
}
