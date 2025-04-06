# Preview Service

면접 연습 및 준비를 위한 서비스 백엔드 애플리케이션입니다.

## 개발 환경 설정

### 요구 사항

- Java 17 이상
- Gradle 7.6 이상
- MySQL 8.0 (프로덕션 환경)
- H2 Database (로컬 개발 환경)

### 로컬 환경에서 실행하기

1. 로컬 프로필로 애플리케이션 실행:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

또는 JAR 파일 생성 후 실행:

```bash
./gradlew build
java -jar build/libs/preview-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

2. H2 콘솔 접속:
   - URL: http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:previewdb
   - 사용자명: sa
   - 비밀번호: (빈 값)

## 초기 데이터 구성

로컬 환경에서 실행 시 다음 데이터가 자동으로 초기화됩니다:

1. 사용자 플랜 (무료, 스탠다드, 프로)
2. 경력 수준 (신입, 주니어, 미드레벨, 시니어, 임원급)
3. 기타 필요한 기준 데이터

## 프로덕션 환경 설정

프로덕션 환경에서는 외부 MySQL 데이터베이스를 사용합니다. 다음 환경 변수를 설정하세요:

```
SPRING_DATASOURCE_URL=jdbc:mysql://[DB_HOST]:[DB_PORT]/[DB_NAME]
SPRING_DATASOURCE_USERNAME=[DB_USERNAME]
SPRING_DATASOURCE_PASSWORD=[DB_PASSWORD]
```

## API 문서

API 문서는 Swagger UI를 통해 제공됩니다:
- 로컬 환경: http://localhost:8080/swagger-ui.html
- 개발 환경: https://dev-api.evawova.com/swagger-ui.html
- 프로덕션 환경: https://api.evawova.com/swagger-ui.html 