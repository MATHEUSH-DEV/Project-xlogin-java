/**
 * Utility class for secure password management.
 * Uses BCrypt adaptive hashing to protect against brute-force attacks.
 * Standard compliance for modern software engineering. 
 * Author: MATHEUSH-DEV
 */

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    // Transforma a senha em Hash antes de salvar
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Compara a senha digitada com o hash salvo no banco
    public static boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}