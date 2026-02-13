import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

public class Xlogin {

    // 1. USER STATUS ENUMERATION FOR BETTER READABILITY AND MAINTENANCE
    public enum UserStatus {
        OFFLINE(0), ONLINE(1), AWAY(2), BANNED(3);

        private final int value;

        UserStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UserStatus fromInt(int i) {
            for (UserStatus s : UserStatus.values()) {
                if (s.getValue() == i)
                    return s;
            }
            return OFFLINE;
        }
    }

    public void autenticar(String user, String pass) {
        String url = "jdbc:sqlite:kronus_local.db";
        
        String sql = "SELECT * FROM usuarios WHERE username = ?";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, user.trim());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // MUDANÇA 2: Pegamos o hash que você gerou no registro
                    String hashBanco = rs.getString("password");

                    // MUDANÇA 3: O Java valida se a senha bate com o hash
                    if (PasswordHasher.checkPassword(pass.trim(), hashBanco)) {

                        // Agora pegamos o status com segurança
                        int statusDoBanco = rs.getInt("status");

                        if (statusDoBanco == UserStatus.BANNED.getValue()) {
                            JOptionPane.showMessageDialog(null, "Your account is banned!", "Security Risk",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        JOptionPane.showMessageDialog(null, "Login Success! Welcome to Kronus Rift.");
                        // Aqui você chamaria sua próxima tela

                    } else {
                        // Senha errada
                        JOptionPane.showMessageDialog(null, "User or Password incorrect!");
                    }
                } else {
                    // Usuário não existe
                    JOptionPane.showMessageDialog(null, "User or Password incorrect!");
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro: Driver SQLite não encontrado!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Kronus Login");
        frame.setSize(250, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        JLabel inicio = new JLabel("Kronus Login v1.0");
        JButton botao = new JButton("Entrar");
        JLabel login = new JLabel("Login: ");
        JLabel senha = new JLabel("Senha: ");

        JTextArea navbar1 = new JTextArea("Digite seu username");
        JPasswordField navbar2 = new JPasswordField("");

        Xlogin engine = new Xlogin();

        botao.addActionListener(e -> {
            String usuarioDigitado = navbar1.getText();
            String senhaDigitada = new String(navbar2.getPassword());
            engine.autenticar(usuarioDigitado, senhaDigitada);
        });

        JButton botao2 = new JButton("Registrar");
        Xregister logica = new Xregister();

        botao2.addActionListener(e -> {
            logica.abrirTela();
            frame.dispose();
        });

        configurarPlaceholder(navbar1, "Digite seu username");

        inicio.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        login.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));
        senha.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));

        inicio.setBounds(30, 10, 180, 50);
        login.setBounds(20, 70, 50, 20);
        navbar1.setBounds(70, 70, 140, 20);
        senha.setBounds(15, 110, 60, 20);
        navbar2.setBounds(70, 110, 140, 20);

        botao.setBounds(60, 165, 120, 30);
        botao2.setBounds(60, 215, 120, 30);

        frame.add(inicio);
        frame.add(login);
        frame.add(senha);
        frame.add(navbar1);
        frame.add(navbar2);
        frame.add(botao);
        frame.add(botao2);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void configurarPlaceholder(JTextArea campo, String textoPadrao) {
        campo.setForeground(Color.GRAY);
        campo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(textoPadrao)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(textoPadrao);
                }
            }
        });
    }
}