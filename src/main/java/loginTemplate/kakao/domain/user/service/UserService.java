package loginTemplate.kakao.domain.user.service;


import loginTemplate.kakao.domain.user.dao.UserRepository;
import loginTemplate.kakao.domain.user.domain.User;
import loginTemplate.kakao.global.error.ApplicationException;
import loginTemplate.kakao.global.error.UserErrorCode;
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
