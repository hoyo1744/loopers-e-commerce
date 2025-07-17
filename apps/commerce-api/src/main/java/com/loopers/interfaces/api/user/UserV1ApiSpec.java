package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API", description = "User API")
public interface UserV1ApiSpec {


    @Operation(
            summary = "회원 조회",
            description = "ID로 User를 조회합니다."
    )
    ApiResponse<UserResponse.User> getUser(
            @Schema(name = "id", description = "User ID", example = "hoyong.eom" )
            String userId);


    @Operation(
            summary = "회원 가입",
            description = "id, password, name, email, phonNumber, birthDate, gender 정보를 입력 받아 회원 정보를 등록합니다."
    )
    ApiResponse<UserResponse.User> signUpUser(
            UserRequest.SignUp signUp);

 }
