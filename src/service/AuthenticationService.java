package service;

import dao.UserDao;
import model.User;
import model.UserStatus;
import util.PasswordHasher;

import java.io.IOException;

public class AuthenticationService {
    private final UserDao userDao = new UserDao();

    /**
     * Tenta autenticar o usuário e retorna o objeto corresponente quando bem‑sucedido.
     * Retorna null se usuário/senha estiverem incorretos.
     * Lança IllegalStateException caso o usuário esteja banido.
     */
    public User authenticate(String username, String password) throws Exception {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            return null;
        }
        if (user.getStatus() == UserStatus.BANNED) {
            throw new IllegalStateException("User is banned");
        }
        return user;
    }

    public void launchLobby(int userId) throws IOException {
        GameLauncher.launchLobby(userId);
    }
}
