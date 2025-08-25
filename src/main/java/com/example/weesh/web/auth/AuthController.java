package com.example.weesh.web.auth;

import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.auth.application.useCase.LoginUseCase;
import com.example.weesh.core.auth.application.useCase.ProfileUseCase;
import com.example.weesh.core.auth.application.useCase.TokenManagementUseCase;
import com.example.weesh.core.auth.exception.AuthErrorCode;
import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.data.jwt.JwtTokenResponse;
import com.example.weesh.web.auth.dto.AuthRequestDto;
import com.example.weesh.web.auth.dto.LogoutResponseDto;
import com.example.weesh.web.auth.dto.ProfileResponseDto;
import com.example.weesh.web.user.dto.UserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
@RequestMapping("/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final ProfileUseCase profileUseCase;
    private final TokenManagementUseCase tokenManagementUseCase;
    private final TokenResolver tokenResolver;
    private final TokenStorage tokenStorage;
    private final TokenValidator tokenValidator; // DIP 준수

    @Operation(summary = "로그인", description = "토큰 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx",
                    description = "실패"
            )
    })
    @Parameters({
            @Parameter(name = "username", description = "아이디", example = "user123"),
            @Parameter(name = "password", description = "비밀번호", example = "password1234!"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 요청 DTO",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthRequestDto.class)
            )
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> loginWithValidation(@Valid @RequestBody AuthRequestDto requestLogin) {
        JwtTokenResponse response = loginUseCase.login(requestLogin);
        LoggingUtil.info("User {} logged in successfully", requestLogin.getUsername());
        return ResponseEntity
                .ok(ApiResponse
                        .success("로그인 성공", response));
    }

    @Operation(summary = "마이프로필", description = "내 정보 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx",
                    description = "실패"
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(HttpServletRequest request) {
        String accessToken = tokenResolver.resolveToken(request);
        tokenValidator.validateToken(accessToken);
        ProfileResponseDto response = profileUseCase.getProfileWithPortfolios(tokenValidator.getUsername(accessToken));
        LoggingUtil.info("Profile retrieved for user {}", tokenValidator.getUsername(accessToken));
        return ResponseEntity
                .ok(ApiResponse
                        .success("프로필 조회 성공", response));

    }

    @Operation(summary = "로그인 연장", description = "토큰 재 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx",
                    description = "실패"
            )
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> reissueToken(HttpServletRequest request) {
        String refreshToken = tokenResolver.resolveRefreshToken(request);
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.MISSING_TOKEN, "리프레시 토큰이 필요합니다.");
        }

        String newAccessToken = tokenManagementUseCase.reissueAccessToken(refreshToken);
        String username = tokenValidator.getUsername(refreshToken);

        JwtTokenResponse response = JwtTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(tokenStorage.getStoredRefreshToken(username))
                .tokenType("Bearer")
                .accessTokenExpireDate(30 * 60 * 1000L) // 30분
                .build();

        LoggingUtil.info("Access token reissued for user {}", username);
        return ResponseEntity
                .ok(ApiResponse
                        .success("토큰 재발급 성공", response));
    }

    @Operation(summary = "로그아웃", description = "토큰 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx",
                    description = "실패"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponseDto>> logout(HttpServletRequest request) {
        String accessToken = tokenResolver.resolveToken(request); // resolveToken 메서드 추가 필요
        tokenManagementUseCase.logout(accessToken);
        String username = tokenValidator.getUsername(accessToken);
        return ResponseEntity
                .ok(ApiResponse
                        .success("로그아웃 성공", null));
    }
}