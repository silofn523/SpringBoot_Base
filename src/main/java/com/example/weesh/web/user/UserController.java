package com.example.weesh.web.user;

import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.core.user.application.useCase.RegisterUserUseCase;
import com.example.weesh.web.user.dto.UserRequestDto;
import com.example.weesh.web.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
@RequestMapping("/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;

    @Operation(summary = "회원가입", description = "사용자 추가")
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
            @Parameter(name = "fullName", description = "이름", example = "홍길동"),
            @Parameter(name = "studentNumber", description = "학번", example = "3204"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 요청 DTO",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserRequestDto.class)
            )
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto response = registerUserUseCase.register(userRequestDto);
        LoggingUtil.info("User {} registered successfully", userRequestDto.getUsername());
        return ResponseEntity
                .ok(ApiResponse
                        .success("회원가입 성공",response)
        );
    }

    @Operation(summary = "관리자 회원가입", description = "관리자 추가")
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
            @Parameter(name = "username", description = "아이디", example = "admin123"),
            @Parameter(name = "password", description = "비밀번호", example = "password1234!"),
            @Parameter(name = "fullName", description = "이름", example = "홍길동"),
            @Parameter(name = "studentNumber", description = "학번", example = "3204"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 요청 DTO",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserRequestDto.class)
            )
    )
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<Object>> registerAdmin(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto response = registerUserUseCase.registerAdmin(userRequestDto);
        LoggingUtil.info("Admin {} registered successfully", userRequestDto.getUsername());
        return ResponseEntity
                .ok(ApiResponse
                        .success("관리자 등록 성공", response));
    }
}
