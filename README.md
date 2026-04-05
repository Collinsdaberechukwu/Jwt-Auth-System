# Jwt-Auth-System
JwtAuthSystem is a Spring Boot-based backend authentication system that provides secure user registration, login, and role-based authorization using JWT (JSON Web Tokens).



## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Class: UserServiceImpl](#class-userserviceimpl)
- [Endpoints / Usage](#endpoints--usage)
- [Dependencies](#dependencies)
- [Error Handling](#error-handling)

---

## Overview

`UserServiceImpl` implements the `UserService` interface, providing the following responsibilities:

- Register new users with role assignment (`USER` by default).
- Authenticate users and generate JWT tokens.
- Handle login attempts with account locking after multiple failures.
- Provide user retrieval methods (`findByEmail` and `getAllUsers`) for role-based access control.
- Implement rate limiting on login attempts using **Resilience4j**.

The service ensures consistent responses via `ResponseDto` objects for all endpoints.

---

## Features

1. **User Registration**
   - Checks if a user with the provided email already exists.
   - Hashes passwords using `PasswordEncoder`.
   - Assigns a default role (`USER`) on registration.
   - Publishes a `UserCreatedEvent` on successful registration.
   - Returns a structured response using `ResponseDto<UserCreationResponse>`.

2. **User Login**
   - Authenticates using `AuthenticationManager`.
   - Generates JWT tokens on successful login.
   - Implements account lock after 3 failed attempts for 15 minutes.
   - Publishes a `LoginCreatedEvent` on failed login attempts.
   - Rate-limited using `@RateLimiter` with a fallback for too many requests.

3. **User Retrieval**
   - `findByEmail(String email)`: Fetches a user by email and returns a structured `ResponseDto<User>`.
   - `getAllUsers()`: Returns all users, suitable for admin endpoints.

---

## Class: `UserServiceImpl`

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;

    @Override
    public ResponseEntity<ResponseDto<UserCreationResponse>> registerUser(UserCreationRequest creationRequest) { ... }

    @RateLimiter(name = "authLimiter", fallbackMethod = "loginRateLimitFallback")
    @Override
    public ResponseEntity<UserLoginResponse> loginUser(LoginRequest loginRequest) { ... }

    public ResponseEntity<UserLoginResponse> loginRateLimitFallback(LoginRequest loginRequest, Throwable t) { ... }

    @Override
    public ResponseEntity<ResponseDto<User>> findByEmail(String email) { ... }

    @Override
    public ResponseEntity<ResponseDto<List<User>>> getAllUsers() { ... }
}

Key points:

@Slf4j is used for logging user activity.
@RateLimiter handles brute-force protection for login endpoints.
Events (UserCreatedEvent & LoginCreatedEvent) are published for async processing.
Endpoints / Usage

The service is exposed via the AuthController:

Endpoint	Access	Description
/api/v1/users/register_user	Public	Register a new user
/api/v1/users/login	Public	Authenticate user & return JWT token
/api/v1/users/user/me	Authenticated	Retrieve user by email
/api/v1/users/admin/users	Admin only	Retrieve all users
/api/v1/users/public/health	Public	Health check endpoint

Note: Role-based access is enforced using Spring Security annotations:

@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
@PreAuthorize("hasAuthority('ADMIN')")
Dependencies
Spring Boot 3
Spring Security
Resilience4j (Rate Limiter)
JWT (JSON Web Tokens)
Lombok (@RequiredArgsConstructor, @Slf4j, etc.)
Spring Data JPA (Repositories)
MySQL / PostgreSQL (Database)
Error Handling

The service provides structured error responses using exceptions:

Exception	When it occurs
UserAlreadyExistException	Duplicate registration attempt
ResourceNotFoundException	User not found for email queries
InvalidLoginException	Account locked due to multiple failed logins
BadCredentialsException	Invalid password
RateLimitException	Login attempts exceeded limit (via fallback)

Each exception results in a meaningful HTTP status and message to the client.

Author
Collins Okafor
Jwt-Auth-System System
