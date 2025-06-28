package com.hackerton.cf.domain.user.service;


import com.hackerton.cf.domain.user.dao.UserRepository;
import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.global.error.ApplicationException;
import com.hackerton.cf.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);


    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.NOTFOUND_USER));
    }

}
