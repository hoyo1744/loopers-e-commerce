package com.loopers.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfo.User getUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        return UserInfo.User.of(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getBirthDate().toString(), user.getGender().getValue());
    }

    public UserInfo.User signUpUser(User user) {
        User signUpUser = userRepository.save(user);
        return UserInfo.User.of(signUpUser.getId(),signUpUser.getName(), signUpUser.getEmail(), signUpUser.getPhoneNumber(),
                signUpUser.getBirthDate().toString(), signUpUser.getGender().getValue());
    }
}
