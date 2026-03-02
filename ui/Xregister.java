package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import service.RegistrationService;
import util.UIUtils;

public class Xregister {
    private final RegistrationService regService = new RegistrationService();

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
        UIUtils.configurePlaceholder(navbarlogin, "Digite seu username");
        UIUtils.configurePlaceholder(navbaremail, "Digite seu email");
        UIUtils.configurePlaceholder(passwordField, "Digite sua senha");
        UIUtils.configurePlaceholder(navbarconfirmPasswordField, "Confirme sua senha");

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

            try {
                regService.register(username, email, password, confirmPassword);
                JOptionPane.showMessageDialog(frame, "Registro bem-sucedido!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                Xlogin.main(null);
                frame.dispose();
            } catch (Exception ex) {
                String msg = ex.getMessage();
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
}
