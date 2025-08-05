package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfo.User getUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자 ID가 입력되지 않았습니다.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        return UserInfo.User.of(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getBirthDate().toString(), user.getGender().getValue());
    }

    public UserInfo.User signUpUser(User user) {
        userRepository.findById(user.getId())
                .ifPresent(existingUser -> {
                    throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 사용자입니다. id=" + existingUser.getId());
                });

        User signUpUser = userRepository.save(user);
        return UserInfo.User.of(signUpUser.getId(),signUpUser.getName(), signUpUser.getEmail(), signUpUser.getPhoneNumber(),
                signUpUser.getBirthDate().toString(), signUpUser.getGender().getValue());
    }
}
