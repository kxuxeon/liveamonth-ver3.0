package teamproject.lam_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import teamproject.lam_server.domain.User;
import teamproject.lam_server.repository.LoginRepository;

import java.util.HashMap;
import java.util.Map;

import static teamproject.lam_server.constants.AttrConstants.*;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;

    private final PasswordEncoder passwordEncoder;

    public SingleResult<String> login(String loginId, String password) {
//        return loginRepository.findUserByLoginId(loginid)
//                .filter(m -> m.getPassword().equals(password))
//                .orElse(null);
        return loginRepository.findUserByLoginId(loginId)
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElse(null);
    }

    public User findId(String name, String email) {
        return loginRepository.findUserByNameAndEmail(name, email)
                .orElse(null);
    }

    public Map<String, Object> findPw(String loginId, String email) {
        return this.editPassword(loginRepository.findUserByLoginIdAndEmail(loginId, email).orElse(null));
    }

    private Map<String, Object> editPassword(User user) {
        if (user == null) return null;
        else {
            Map<String, Object> result = new HashMap<>();
            String temporaryPw = "";
            for (int i = 0; i < 8; i++) {
                temporaryPw += (char) ((Math.random() * 26) + 97);
            }
            loginRepository.editPassword(user, passwordEncoder.encode(temporaryPw));
            result.put(EMAIL, user.getEmail());
            result.put(NAME, user.getName());
            result.put(TEMPORARY_PW, temporaryPw);
            return result;
        }
    }
}
