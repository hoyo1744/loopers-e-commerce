package com.loopers.application.user;

import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class UserFacade {
    private final UserService userService;

    public UserResult.User getUser(String userId) {
        UserInfo.User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        return UserResult.User.from(user);
    }

    @Transactional
    public UserResult.User signUpUser(UserCommand.SignUp signUpUserCommand) {
        UserInfo.User signUpUser = userService.signUpUser(signUpUserCommand.toDomainUser());
        return UserResult.User.from(signUpUser);
    }

}
