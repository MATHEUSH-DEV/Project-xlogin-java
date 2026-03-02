package service;

import dao.UserDao;
import model.User;
import model.UserStatus;
import util.PasswordHasher;

import java.sql.SQLException;

public class RegistrationService {
    private final UserDao userDao = new UserDao();

    public void register(String username, String email, String password, String confirmPassword) throws Exception {
        if (username == null || username.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos!");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }
        String hash = PasswordHasher.hashPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setStatus(UserStatus.OFFLINE);
        boolean ok = userDao.insert(user);
        if (!ok) {
            throw new SQLException("Falha ao inserir usuário.");
        }
    }
}
