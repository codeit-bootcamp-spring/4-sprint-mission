package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Refresh Token 문자열 (암호화 저장 권장)
   */
  @Column(nullable = false, unique = true)
  private String token;

  /**
   * 해당 토큰이 속한 사용자 식별자
   */
  @Column(nullable = false, unique = true)
  private UUID userId;

  /**
   * 토큰 만료 시각
   */
  @Column(nullable = false)
  private LocalDateTime expiredAt;

  /**
   * 토큰이 회전(rotation)되었는지 여부
   */
  private boolean rotated;


  public void updateToken(String token, long expirationMinutes) {
    this.token = token;
    this.expiredAt = LocalDateTime.now().plusMinutes(expirationMinutes);
  }
}
