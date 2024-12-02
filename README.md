# Spring Security JWT 프로젝트

이 프로젝트는 **Spring Boot 3**과 **Spring Security**를 사용하여 JWT 기반 인증 및 인가를 구현하는 예제입니다. 주로 API 엔드포인트 보호와 사용자 권한 관리에 중점을 두고 있습니다. JWT를 통해 인증을 수행하고, Refresh Token을 사용해 세션 유지를 관리합니다.

---

## 주요 기능
- JWT 기반 인증 구현
- Access Token과 Refresh Token 발급 및 관리
- 로그인, 로그아웃, 사용자 정보 조회 기능 구현
- 토큰 유효성 검증 및 갱신

---

## 프로젝트 구조
- **AuthController**: 사용자 로그인, 토큰 갱신, 로그아웃을 처리하는 컨트롤러
- **TokenProvider**: JWT 토큰 생성, 검증, 유효기간 관리
- **RefreshTokenRepository**: RefreshToken을 저장하고 관리하는 JPA 레포지토리
- **AuthenticationService**: 로그인 및 인증 관련 로직을 처리하는 서비스

---

## 설정 파일 (application.yml)

JWT 관련 설정은 `application.yml`에서 관리합니다. 이 파일에서 JWT 비밀 키, Access Token과 Refresh Token의 유효기간을 정의합니다.

```yaml
jwt:
  secret: your-secret-key
  token-validity-in-seconds: 1800  # AccessToken 유효 시간 (초 단위): 30분
  refreshtoken-validity-in-seconds: 604800  # RefreshToken 유효 시간 (초 단위): 7일
```

---

## 주요 클래스 및 메소드 설명

### 1. TokenProvider 클래스
`TokenProvider` 클래스는 JWT 토큰을 생성하고 검증하는 기능을 담당합니다. `secret` 키를 사용해 서명하고, 만료 시간을 설정하여 토큰의 유효성을 관리합니다.

#### TokenProvider 필드 설명
- `secret`: JWT 서명에 사용할 비밀 키로, `application.yml`에서 주입됩니다.
- `accessTokenValidityInMilliseconds`: AccessToken 유효시간(밀리초)입니다.
- `refreshTokenValidityInMilliseconds`: RefreshToken 유효시간(밀리초)입니다.
- `refreshTokenRepository`: RefreshToken을 관리하기 위한 JPA 레포지토리입니다.

#### 주요 메소드
```java
public String createToken(Authentication authentication, boolean isAccessToken)
```
- `authentication`: 인증 정보를 담고 있으며, 사용자의 권한 정보가 포함되어 있습니다.
- `isAccessToken`: AccessToken을 생성할지, RefreshToken을 생성할지를 결정하는 boolean 값입니다.

이 메소드는 JWT 토큰을 생성하며, 권한을 포함한 클레임을 설정하고 만료 시간을 지정합니다.

```java
public String createAndPersistRefreshTokenForUser(Authentication authentication)
```
- 인증 정보를 사용해 RefreshToken을 생성하고, 이를 DB에 저장합니다.
- RefreshToken은 세션 유지를 위해 필요하며, 유효 기간은 `application.yml`에 정의된 시간입니다.

```java
public Authentication getAuthentication(String token)
```
- 토큰의 유효성을 검증하고, 유효한 경우 `Authentication` 객체를 반환합니다. 이 객체는 SecurityContext에 저장되어 사용자 요청을 인증하는 데 사용됩니다.

### 2. AuthController 클래스
`AuthController`는 사용자 로그인, 토큰 갱신, 로그아웃을 처리하는 API 엔드포인트를 제공합니다.

#### 로그인 메소드
```java
@PostMapping("/login")
public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginDto loginDto)
```
- `loginDto`에는 사용자의 ID와 비밀번호가 포함되어 있습니다.
- 로그인 성공 시 AccessToken과 RefreshToken을 발급하며, 이를 응답에 포함합니다.

#### 리프레시 토큰으로 토큰 갱신
```java
@PostMapping("/refresh-token")
public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest, Authentication authentication)
```
- `refreshTokenRequest`에는 클라이언트로부터 받은 RefreshToken이 포함됩니다.
- RefreshToken이 유효한 경우 새 AccessToken을 생성하고 응답으로 반환합니다.
- 유효하지 않은 경우에는 400 오류와 함께 만료 메시지를 반환합니다.

#### 로그아웃 메소드
```java
@PostMapping("/logout")
public ResponseEntity<String> logout(@RequestBody LogoutDto logoutDto)
```
- 로그아웃 요청 시 RefreshToken을 받아 이를 DB에서 삭제합니다.
- RefreshToken이 존재하지 않으면 400 오류와 함께 오류 메시지를 반환합니다.

---

## 엔드포인트 사용 방법

### 1. 로그인
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
  - 성공 시, AccessToken과 RefreshToken을 포함한 응답이 반환됩니다.
  ```json
  {
    "accessToken": "access-token-value",
    "refreshToken": "refresh-token-value"
  }
  ```

### 2. 토큰 갱신
- **URL**: `/api/refresh-token`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "refreshToken": "refresh-token-value"
  }
  ```
- **Response**:
  - 유효한 RefreshToken을 제공하면 새로운 AccessToken이 반환됩니다.
  ```json
  {
    "accessToken": "new-access-token-value"
  }
  ```

### 3. 로그아웃
- **URL**: `/api/logout`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "refreshToken": "refresh-token-value"
  }
  ```
- **Response**:
  - 성공 시 "Successfully logged out" 메시지가 반환됩니다.

### 4. 사용자 정보 조회
- **본인 정보 조회**:
  - **URL**: `/api/user`
  - **Method**: `GET`
  - **Headers**: `Authorization: Bearer <access-token>`
  - **권한 필요**: `ROLE_USER` 또는 `ROLE_ADMIN`

- **특정 사용자 정보 조회** (관리자 전용):
  - **URL**: `/api/user/{username}`
  - **Method**: `GET`
  - **Headers**: `Authorization: Bearer <access-token>`
  - **권한 필요**: `ROLE_ADMIN`

---

## 사용 기술
- **Spring Boot 3**: 빠르고 효율적인 웹 애플리케이션 개발 프레임워크
- **Spring Security**: 인증 및 권한 관리
- **JWT (JSON Web Token)**: 인증에 필요한 토큰 생성 및 관리
- **JPA (Java Persistence API)**: 데이터베이스 연동 및 엔티티 관리

---

## 프로젝트 실행 방법

1. `application.yml` 파일에서 JWT 관련 설정과 데이터베이스 설정을 입력합니다.
2. 프로젝트를 빌드하고 실행합니다.
3. Postman 등을 사용하여 `/login`, `/refresh-token`, `/logout`, `/user` 엔드포인트를 통해 API 요청을 테스트합니다.
