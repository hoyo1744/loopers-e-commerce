package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserResult;
import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @Override
    @GetMapping("/me")
    public ApiResponse<UserResponse.User> getUser(@RequestHeader(value = "X-USER-ID") String id) {
        UserResult.User user =
                userFacade.getUser(id);

        return ApiResponse.success(UserResponse.User.from(user));
    }

    @Override
    @PostMapping
    public ApiResponse<UserResponse.User> signUpUser(@Valid @RequestBody UserRequest.SignUp signUp) {
        UserResult.User user =
                userFacade.signUpUser(signUp.toAppUserCommand());

        return ApiResponse.success(UserResponse.User.from(user));
    }
}
