package com.example.weesh.web.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "로그인 DTO")
public class AuthRequestDto {
    @Schema(description = "아이디", example = "user123")
    @NotNull(message = "아이디는 필수 입력 값입니다.")
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    @Schema(description = "비밀번호", example = "Password123!")
    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
