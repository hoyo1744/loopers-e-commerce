package com.loopers.application.user;

import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public AppUserResult.User getUser(String userId) {
        UserInfo.User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        return AppUserResult.User.from(user);
    }

    public AppUserResult.User signUpUser(AppUserCommand.SignUp signUpUserCommand) {
        UserInfo.User findUser = userService.getUser(signUpUserCommand.getId());
        if (findUser != null) {
            throw new CoreException(ErrorType.CONFLICT, "이미 등록된 회원입니다.");
        }
        UserInfo.User signUpUser = userService.signUpUser(signUpUserCommand.toDomainUser());
        return AppUserResult.User.from(signUpUser);
    }

}
