# 인터뷰 서비스 도메인 추가 가이드라인

## 1. 패키지 구조

새 도메인은 다음 패키지 구조를 따릅니다:

- com.evawova.preview.domain.{도메인명}
  - controller: REST 엔드포인트 정의
  - dto: 데이터 전송 객체
  - entity: JPA 엔티티 클래스
  - model: Enum 및 상수 정의
  - repository: 데이터 액세스 계층
  - service: 비즈니스 로직

## 2. 네이밍 규칙

- 클래스: Pascal Case (예: InterviewSession, JobRole)
- 메서드/변수: Camel Case (예: findByCategory, createPrompt)
- 상수: Snake Case 대문자 (예: MAX_RETRY_COUNT)
- 패키지: 모두 소문자 (예: com.evawova.preview.domain.interview)
- Enum: 대문자와 언더스코어 (예: FRONTEND_DEVELOPER, UI_UX_DESIGNER)

## 3. 엔티티 설계

- @Entity, @Table 애노테이션 사용
- @Comment로 각 필드 설명 추가
- @Id, @GeneratedValue로 PK 설정
- 필수 필드는 @Column(nullable = false) 적용
- Lombok @Getter, @Builder 패턴 적용
- 생성자는 @NoArgsConstructor(access = AccessLevel.PROTECTED) 및 @Builder 조합 사용
- 생성/수정 시간 자동 관리 (createdAt, updatedAt)
- 비즈니스 로직은 엔티티 내 메서드로 구현 (예: deactivate())

## 4. DTO 설계

- 엔티티와 1:1 매핑되는 DTO 클래스 생성
- @Getter, @Builder 애노테이션 사용
- 정적 팩토리 메서드 from()으로 엔티티→DTO 변환 제공
- 표현 계층에 필요한 필드만 포함
- 유효성 검증은 @Valid와 관련 애노테이션 사용

## 5. 서비스 계층

- @Service, @RequiredArgsConstructor 애노테이션 사용
- 읽기 전용 메서드는 @Transactional(readOnly = true) 적용
- 수정 메서드는 @Transactional 적용
- 생성자 주입 방식으로 의존성 주입
- 비즈니스 로직을 캡슐화하고 컨트롤러에 노출

## 6. 컨트롤러 설계

- @RestController, @RequestMapping("/api/v1/{도메인명}") 사용
- @PreAuthorize로 접근 제어 적용 (최소한 "hasAnyRole('USER_FREE', 'USER_STANDARD', 'USER_PRO', 'ADMIN')")
- Swagger 문서화 (@Tag, @Operation, @Parameter)
- 표준 HTTP 상태 코드 및 응답 포맷 준수
- 예외는 전역 예외 핸들러로 처리

## 7. 리포지토리

- JpaRepository 확장
- 필요한 쿼리 메서드 명명규칙 준수 (findBy, countBy 등)
- 복잡한 쿼리는 @Query 애노테이션 사용

## 8. Enum 활용

- 반복되는 상수나 카테고리는 Enum으로 모델링
- Enum에 displayName 필드와 getter 추가
- JPA에서는 @Enumerated(EnumType.STRING) 사용

## 9. 초기 데이터

- 도메인 관련 초기 데이터는 InitService에 추가
- 카테고리별 switch 구문으로 상세 내용 작성

## 10. 문서화

- 클래스/메서드에 Javadoc 주석 추가
- 엔티티 필드는 @Comment로 설명 추가
- API는 Swagger 애노테이션으로 문서화
- 복잡한 비즈니스 로직에 주석 추가

## 11. 테스트

- 각 계층별 단위 테스트 작성
- 통합 테스트로 전체 흐름 검증
- 테스트 데이터 팩토리 메서드 활용

## 12. 보안 및 권한

- API 엔드포인트는 적절한 권한 체크 적용
- 민감 데이터는 암호화 처리
- 사용자 식별과 권한 검증 철저히 적용
- application.yml 설정 시 민감 데이터는 .env 사용

## 13. 예시 코드

### 엔티티 예시

```java
@Entity
@Table(name = "example_entity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Comment("예시 이름")
    private String name;

    @Column(nullable = false)
    @Comment("예시 상태")
    @Enumerated(EnumType.STRING)
    private ExampleStatus status;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    public void updateStatus(ExampleStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
```

### DTO 예시

```java
@Getter
@Builder
public class ExampleDto {
    private Long id;
    private String name;
    private ExampleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExampleDto from(ExampleEntity entity) {
        return ExampleDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
```

### 서비스 예시

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExampleService {
    private final ExampleRepository exampleRepository;

    public ExampleDto getExample(Long id) {
        return ExampleDto.from(exampleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Example not found")));
    }

    @Transactional
    public ExampleDto updateStatus(Long id, ExampleStatus newStatus) {
        ExampleEntity entity = exampleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Example not found"));
        entity.updateStatus(newStatus);
        return ExampleDto.from(entity);
    }
}
```

### 컨트롤러 예시

```java
@RestController
@RequestMapping("/api/v1/examples")
@Tag(name = "Example", description = "예시 API")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService exampleService;

    @GetMapping("/{id}")
    @Operation(summary = "예시 조회")
    @PreAuthorize("hasAnyRole('USER_FREE', 'USER_STANDARD', 'USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExampleDto>> getExample(
            @Parameter(description = "예시 ID") @PathVariable Long id) {
        return ResponseEntity.ok(exampleService.getExample(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "예시 상태 수정")
    @PreAuthorize("hasAnyRole('USER_STANDARD', 'USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExampleDto>> updateStatus(
            @Parameter(description = "예시 ID") @PathVariable Long id,
            @Parameter(description = "새로운 상태") @RequestParam ExampleStatus status) {
        return ResponseEntity.ok(exampleService.updateStatus(id, status));
    }
}
```
