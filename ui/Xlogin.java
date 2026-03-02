package ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import model.User;
import service.AuthenticationService;
import util.UIUtils;

public class Xlogin extends javax.swing.JFrame {

    private final AuthenticationService authService = new AuthenticationService();

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
            try {
                User user = engine.authService.authenticate(usuarioDigitado, senhaDigitada);
                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Login Success! Welcome to Kronus Rift.");
                    engine.authService.launchLobby(user.getId());
                    engine.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "User or Password incorrect!");
                }
            } catch (IllegalStateException ban) {
                JOptionPane.showMessageDialog(null, "Your account is banned!", "Security Risk", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            }
        });

        JButton botao2 = new JButton("Registrar");
        botao2.addActionListener(e -> {
            Xregister logica = new Xregister();
            logica.abrirTela();
            frame.dispose();
        });

        UIUtils.configurePlaceholder(navbar1, "Digite seu username");

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
}
