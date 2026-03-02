import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Xregister {

    public void abrirTela() {
        JFrame frame = new JFrame("Kronus Register v1.0");
        frame.setSize(750, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        // Elementos
        JLabel label1 = new JLabel("Kronus REGISTRO v1.0");
        JLabel xxlogin = new JLabel("Username: ");
        JLabel xxPass = new JLabel("Password: ");
        JLabel xxconfirmanpass = new JLabel("Confirmar Password: ");
        JLabel xxemail = new JLabel("Email: ");

        // botão voltar
        JButton buttonback = new JButton("Voltar");
        buttonback.addActionListener(e -> {
            Xlogin.main(null);
            frame.dispose();
        });

        // nav bar
        JTextField navbarlogin = new JTextField();
        JTextField navbaremail = new JTextField();
        JPasswordField passwordField = new JPasswordField(15);
        JPasswordField navbarconfirmPasswordField = new JPasswordField(15);


        // Gif
        ImageIcon gifIcon = new ImageIcon("sombra.gif");
        JLabel labelGif = new JLabel(gifIcon);

        // Placeholder (Common practice for UI/UX)
        configurarPlaceholder(navbarlogin, "Digite seu username");
        configurarPlaceholder(navbaremail, "Digite seu email");
        configurarPlaceholder(passwordField, "Digite sua senha");
        configurarPlaceholder(navbarconfirmPasswordField, "Confirme sua senha");

        // Adicionar ao frame
        frame.add(label1);
        frame.add(xxlogin);
        frame.add(xxPass);
        frame.add(xxemail);
        frame.add(navbarlogin);
        frame.add(navbaremail);
        frame.add(passwordField);
        frame.add(navbarconfirmPasswordField);
        frame.add(buttonback);
        frame.add(labelGif);
        frame.add(xxconfirmanpass);

        // Estilização (Styling)
        label1.setFont(new Font("Arial", Font.BOLD, 28));
        xxlogin.setFont(new Font("Arial", Font.BOLD, 15));
        xxemail.setFont(new Font("Arial", Font.BOLD, 15));
        xxPass.setFont(new Font("Arial", Font.BOLD, 15));
        xxconfirmanpass.setFont(new Font("Arial", Font.BOLD, 13));

        // Posicionamento
        label1.setBounds(200, 20, 400, 50);
        buttonback.setBounds(10, 10, 80, 25);

        // --- Coluna de Formulário ---
        int colunaLabel = 20;
        int colunaInput = 180;

        // Username
        xxlogin.setBounds(colunaLabel, 100, 120, 30);
        navbarlogin.setBounds(colunaInput, 100, 220, 30);

        // Email
        xxemail.setBounds(colunaLabel, 150, 120, 30);
        navbaremail.setBounds(colunaInput, 150, 220, 30);

        // Password
        xxPass.setBounds(colunaLabel, 200, 120, 30);
        passwordField.setBounds(colunaInput, 200, 220, 30);

        // Confirmar Password
        xxconfirmanpass.setBounds(colunaLabel, 250, 150, 30);
        navbarconfirmPasswordField.setBounds(colunaInput, 250, 220, 30);

        // Botão de finalizar registro
        JButton btnFinalizar = new JButton("Finalizar Registro");

        btnFinalizar.addActionListener(e -> {
            String username = navbarlogin.getText().trim();
            String email = navbaremail.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(navbarconfirmPasswordField.getPassword()).trim();

            // 1. Validação de campos (Sempre primeiro)
            if (username.isEmpty() || username.equals("Digite seu username") ||
                    email.isEmpty() || email.equals("Digite seu email") ||
                    password.isEmpty() || password.equals("Digite sua senha")) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Validação de Senha (Antes do Hash!)
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Gerar o Hash 
            String hashedPassword = PasswordHasher.hashPassword(password);

            // 4. Banco de Dados
            String sql = "INSERT INTO usuarios (username, password, email, status) VALUES (?, ?, ?, 'OFFLINE')";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kronus_local.db");
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.setString(3, email);

                // Executa UMA VEZ e já guarda o resultado
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Registro bem-sucedido!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    Xlogin.main(null);
                    frame.dispose();
                }
            } catch (SQLException ex) {
                String msg = ex.getMessage().contains("UNIQUE") ? "Usuário ou Email já existe!" : ex.getMessage();
                JOptionPane.showMessageDialog(frame, "Erro: " + msg, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnFinalizar.setBounds(colunaInput, 310, 220, 40);
        frame.add(btnFinalizar);

        // --- O GIF ---
        labelGif.setBounds(410, 40, 324, 427);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void configurarPlaceholder(JTextField campo, String textoPadrao) {
        campo.setText(textoPadrao);
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