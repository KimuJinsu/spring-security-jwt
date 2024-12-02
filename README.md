
# 🔐 Spring Security JWT 프로젝트

이 프로젝트는 **Spring Boot 3**과 **Spring Security**를 사용하여 JWT 기반 인증 및 인가를 구현하는 예제입니다.  
주로 API 엔드포인트 보호와 사용자 권한 관리에 중점을 두고 있으며, **JWT**를 통해 인증을 수행하고, **Refresh Token**을 사용해 세션 유지를 관리합니다.

---

## 🌟 주요 기능
- **JWT 기반 인증 구현**: Access Token과 Refresh Token을 발급 및 검증.
- **로그인, 로그아웃**: 사용자 인증 및 세션 종료 기능 제공.
- **사용자 정보 조회**: API 엔드포인트를 통해 사용자 정보를 조회 가능.
- **토큰 갱신**: Refresh Token을 사용하여 새로운 Access Token 발급.

---

## 📂 프로젝트 구조
- **AuthController**: 사용자 로그인, 토큰 갱신, 로그아웃을 처리하는 컨트롤러.
- **TokenProvider**: JWT 토큰 생성, 검증 및 유효 기간 관리.
- **RefreshTokenRepository**: Refresh Token을 저장하고 관리하는 JPA 레포지토리.
- **AuthenticationService**: 로그인 및 인증 관련 로직 처리.

---

## ⚙️ 설정 파일 (application.yml)

JWT 관련 설정은 `application.yml` 파일에서 관리합니다. 이 파일에서 JWT 비밀 키와 Access Token 및 Refresh Token의 유효기간을 정의합니다.

```yaml
jwt:
  secret: your-secret-key
  token-validity-in-seconds: 1800  # AccessToken 유효 시간 (초 단위): 30분
  refreshtoken-validity-in-seconds: 604800  # RefreshToken 유효 시간 (초 단위): 7일
```

---

## 🧩 주요 클래스 및 메소드 설명

### 1. **TokenProvider 클래스**
`TokenProvider` 클래스는 JWT 토큰을 생성하고 검증하는 기능을 담당합니다.

#### 주요 필드
- **`secret`**: JWT 서명에 사용할 비밀 키로, `application.yml`에서 주입됩니다.
- **`accessTokenValidityInMilliseconds`**: Access Token 유효 시간.
- **`refreshTokenValidityInMilliseconds`**: Refresh Token 유효 시간.

#### 주요 메소드
- **`createToken(authentication, isAccessToken)`**:  
  인증 정보를 기반으로 Access Token 또는 Refresh Token 생성.

- **`createAndPersistRefreshTokenForUser(authentication)`**:  
  Refresh Token 생성 후 데이터베이스에 저장.

- **`getAuthentication(token)`**:  
  토큰의 유효성을 검증하고, SecurityContext에 저장할 `Authentication` 객체를 반환.

---

### 2. **AuthController 클래스**
`AuthController`는 로그인, 토큰 갱신, 로그아웃 관련 API 엔드포인트를 제공합니다.

#### 주요 엔드포인트
- **로그인**: 사용자 인증 정보를 기반으로 Access Token과 Refresh Token 발급.
- **토큰 갱신**: Refresh Token을 사용하여 새로운 Access Token을 발급.
- **로그아웃**: Refresh Token을 삭제하여 세션 종료.

---

## 🚀 엔드포인트 사용 방법

### 1. **로그인**
- **URL**: `/api/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "your-username",
    "password": "your-password"
  }
  ```
- **Response**:
  ```json
  {
    "accessToken": "access-token-value",
    "refreshToken": "refresh-token-value"
  }
  ```

### 2. **토큰 갱신**
- **URL**: `/api/refresh-token`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "refreshToken": "refresh-token-value"
  }
  ```
- **Response**:
  ```json
  {
    "accessToken": "new-access-token-value"
  }
  ```

### 3. **로그아웃**
- **URL**: `/api/logout`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "refreshToken": "refresh-token-value"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Successfully logged out"
  }
  ```

### 4. **사용자 정보 조회**
- **본인 정보 조회**:
  - **URL**: `/api/user`
  - **Method**: `GET`
  - **Headers**: `Authorization: Bearer <access-token>`

- **특정 사용자 정보 조회** (관리자 전용):
  - **URL**: `/api/user/{username}`
  - **Method**: `GET`
  - **Headers**: `Authorization: Bearer <access-token>`

---

## 🛠️ 사용 기술
- **Spring Boot 3**: 빠르고 효율적인 웹 애플리케이션 개발 프레임워크.
- **Spring Security**: 인증 및 권한 관리 프레임워크.
- **JWT (JSON Web Token)**: 인증에 필요한 토큰 생성 및 관리.
- **JPA (Java Persistence API)**: 데이터베이스 연동 및 엔티티 관리.

---

## 📝 프로젝트 실행 방법

1. **설정 파일 수정**:  
   `application.yml`에서 JWT 비밀 키와 데이터베이스 설정을 입력합니다.

2. **프로젝트 빌드 및 실행**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **API 테스트**:  
   Postman 등 API 테스트 도구를 사용해 `/login`, `/refresh-token`, `/logout` 엔드포인트를 호출하여 동작을 확인합니다.

---

이 프로젝트는 JWT를 활용한 Spring Security 기반 인증 및 인가의 실습 예제입니다.  
Spring Boot 3의 최신 기능과 함께 보안의 핵심 개념을 체험할 수 있도록 구성되었습니다. 🌟
