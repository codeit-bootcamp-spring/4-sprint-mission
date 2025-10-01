package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.handler.LogoutSuccessHandler;
import com.sprint.mission.discodeit.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final LogoutSuccessHandler logoutSuccessHandler;
  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      DiscodeitUserDetailsService discodeitUserDetailsService) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 자원에 대한 접근 허용
            .requestMatchers("/assets/**", "/favicon.ico").permitAll()  // 정적 자원에 대한 접근 허용
            .requestMatchers("/.well-known/**").permitAll() // DevTools/브라우저가 치는 well-known 요청 허용
            .requestMatchers(HttpMethod.GET, "/", "/index.html", "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST,
                "/api/auth/login",
                "/api/auth/logout",
                "/api/users").permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll() // actuator
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).permitAll() // 스웨거 문서
            .anyRequest().authenticated())
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(logoutSuccessHandler))
        .exceptionHandling(e -> e
            .defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")
            )
            .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN)))
        .sessionManagement(mgmt -> mgmt
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)                 // 동시 세션 1개로 제한
                .maxSessionsPreventsLogin(false)    // true: 두 번째 로그인 "거부"
                .sessionRegistry(sessionRegistry())
            ))
        .rememberMe(remember -> remember
            .key("my-remember-key") // 쿠키 생성 시 사용되는 고정 키
            .tokenValiditySeconds(7 * 24 * 60 * 60) // 쿠키 만료 시간 (7일)
            .rememberMeParameter("remember-me") // 로그인 폼에서 사용하는 파라미터명
            .userDetailsService(discodeitUserDetailsService)
        )
    ;

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public ApplicationRunner adminInitializer(PasswordEncoder passwordEncoder) {
    return args -> {
      boolean adminExists = userRepository.existsByRole(Role.ADMIN);
      if (adminExists) {
        log.info("[AdminInit] ADMIN 계정이 이미 존재합니다. 초기화를 건너뜁니다.");
        return;
      }

      String email = "admin@email.com";
      String name = "admin";
      String rawPw = "admin";

      if (userRepository.existsByEmail(email)) {
        log.warn("[AdminInit] 설정된 이메일({}) 사용자가 이미 있어 ADMIN 생성/변경을 건너뜁니다.", email);
        return;
      }

      User admin = new User(name, email, passwordEncoder.encode(rawPw), null, Role.ADMIN);
      Instant now = Instant.now();
      userRepository.save(admin);


      log.info("[AdminInit] ADMIN 계정을 생성했습니다. email={}", email);
    };
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
    return hierarchy;
  }

  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public static HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
