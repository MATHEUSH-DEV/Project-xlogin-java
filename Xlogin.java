import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Xlogin {
    public static void main(String[] args) {
        JFrame frame = new JFrame("X Login");
        frame.setSize(250, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        // Elementos
        
        JLabel inicio = new JLabel("X Login v1.0");
        JButton botao = new JButton("Entrar");
        JLabel login = new JLabel("Login: ");
        JLabel senha = new JLabel("Senha: ");
        JTextArea navbar1 = new JTextArea("Digite seu username");
        JTextArea navbar2 = new JTextArea("Digite sua senha");

        // Botão de registro
        JButton botao2 = new JButton("Registrar");
        Xregister logica = new Xregister();

        botao2.addActionListener(e -> {
            logica.abrirTela();

            // fechar a aba atual
            frame.dispose();
        });

        

        // Aplica a lógica profissional nos dois campos
        configurarPlaceholder(navbar1, "Digite seu username");
        configurarPlaceholder(navbar2, "Digite sua senha");


        //tamanho JLabel
        inicio.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        login.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));
        senha.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));

        // Posicionamento (setBounds)
        inicio.setBounds(30, 10, 180, 50);

        login.setBounds(20, 70, 50, 20);
        navbar1.setBounds(70, 70, 140, 20);
        
        senha.setBounds(15, 110, 60, 20);
        navbar2.setBounds(70, 110, 140, 20); // Ajustado para alinhar com o label senha
        
        botao.setBounds(60, 165, 100, 30);
        botao2.setBounds(130, 225, 100, 30);

        // Adicionar ao frame
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

    // MÉTODO PROFISSIONAL: Reaproveita a lógica para qualquer campo
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
