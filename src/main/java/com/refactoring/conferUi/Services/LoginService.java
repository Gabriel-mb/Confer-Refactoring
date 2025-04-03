package com.refactoring.conferUi.Services;

import com.refactoring.conferUi.Model.Entity.User;
import com.refactoring.conferUi.dao.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean verification(String username, String rawPassword) {
        Optional<User> userOpt = loginRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return false; // Usuário não encontrado
        }

        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPassword()); // Verifica o hash
    }
}
