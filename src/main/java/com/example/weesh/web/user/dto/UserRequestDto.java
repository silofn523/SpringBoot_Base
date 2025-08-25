package com.example.weesh.web.user.dto;

import com.example.weesh.core.foundation.enums.UserRole;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Schema(description = "회원가입 DTO")
public class UserRequestDto {

    @Schema(description = "아이디", example = "user123")
    @NotBlank(message = "아이디는 비어 있을 수 없습니다.")
    @Size(min = 4, max = 12, message = "아이디는 4~12자 입니다")
    private String username;

    @Schema(description = "비밀번호", example = "Password123!")
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 비어 있을 수 없습니다.")
    private String fullName;

    @Schema(description = "학번", example = "3304")
    @NotBlank(message = "학번은 비어 있을 수 없습니다.")
    @Size(min = 4, max = 4, message = "학번은 4자리 숫자여야 합니다.")
    private String studentNumber;

    @Hidden
    private Set<UserRole> roles;

    // 기본 생성자 (Jackson 시리얼라이제이션용)
    public UserRequestDto() {
    }

}