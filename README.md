
[![codecov](https://codecov.io/github/kdfasdf/4-sprint-mission/branch/sprint8/graph/badge.svg?token=Z4ZWRVXB5J)](https://codecov.io/github/kdfasdf/4-sprint-mission)
### REST API 설계원칙 준수
- 리소스 중심의 URI 설계
  - GET /api/users/{userId}
- 명확한 상태코드 응답 제공
  - request : DELETE /api/users/{userId}
  - response : ResponseEntity.noContent().build()
- 필터링, 정렬, 페이징에 쿼리 파라미터 사용
  - request : /channels?userId=d3ee2929-212b-4077-af84-694a0e69b8e1

### 예외 처리
- Global
  - GlobalHandlerExceptionResolver를 통한 전역 예외 처리
    - ErrorResponse(String message, String code, ins status, List\<FieldError> errors, List\<ConstraintViolationError> violationErrors) 형식으로 응답형식 통일
  - 커스텀 예외를 발생시키는 경우 상황에 맞는 오류 메시지를 전달하여 서버 내부 오류 메시지 노출 최소화
- Controller
  - bean validation을 통한 요청 검증
- Service
  - 도메인과 비즈니스 로직 상황에 맞는 커스텀 예외 발생

### OOP
- 무분별한 @Setter 방지를 통한 캡슐화 원칙 준수
  - 수정 가능한 필드에 대해서 비즈니스적인 의미를 가지는 setter 메서드 구현

### Test Code
- 슬라이스 테스트
  - mockito 라이브러리를 사용하여 controller, repository계층 슬라이스 테스트, 서비스 계층 단위테스트를 통한 계층 별 동작 검증
- 통합 테스트
  - controller 계층 통합테스트를 진행하여 프로덕션 코드 안정성 보장

### Logging
- MDC Interceptor
  - 요청별 고유 ID(requestId) 요청 생성 및 HTTP 메서드, 요청 경로 정보 자동 수집
  - afterCompletion에서 MDC 정리를 통한 메모리 누수 방지
- AOP
  - MethodLoggingAspect를 통한 비즈니스 로직 요청 응답 추적
  - LogParameterFormatter를 통한 로그 출력 형식 표준화
    - 리플렉션을 통한 요청 필드 상세 분석
    - 민감정보 마스킹

### API 문서화
- swagger를 통한 API 문서화
  - 인터페이스로 추상화하여 controller 계층에 non-invasive하도록 문서화

### Docker
- 멀티스테이지 빌드: Build와 Runtime 스테이지 분리로 최종 이미지 크기 최적화 
  - Build 스테이지: Gradle 기반 애플리케이션 컴파일 및 의존성 관리 
  - Runtime 스테이지: Amazon Corretto JDK17 경량 런타임 환경 
- 레이어 캐싱 최적화: 의존성과 소스코드 복사 단계 분리로 빌드 성능 향상 
  - Gradle 설정 파일 우선 복사 후 의존성 다운로드 
  - 소스코드 변경 시에도 의존성 레이어 캐시 재사용

### CI/CD
- GitHub Actions를 통한 자동화된 빌드 및 배포 파이프라인 구성 
  - Pull Request 시 자동 테스트 실행 및 코드 커버리지 측정
  - CI 
    - JDK 17 (Amazon Corretto) 기반 빌드 환경 구성 
    - Gradle 캐싱을 통한 빌드 성능 최적화
  - CD 
    - Docker 기반 컨테이너화된 애플리케이션 배포 
    - AWS ECR를 통한 컨테이너 이미지 저장 및 ECS를 정의를 통한 배포

