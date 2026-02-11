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

public class Xregister {

    public void abrirTela() {
        JFrame frame = new JFrame("Kronus Register v1.0");
        frame.setSize(550, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        // Elementos
        JLabel label1 = new JLabel("Kronus REGISTRO v1.0");
        JLabel xxlogin = new JLabel("username: ");
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

        // Gif
        ImageIcon gifIcon = new ImageIcon("sombra.gif");
        JLabel labelGif = new JLabel(gifIcon);


        
        // Placeholder (Common practice for UI/UX)
        configurarPlaceholder(navbarlogin, "Digite seu username");
        configurarPlaceholder(navbaremail, "Digite seu email");
        configurarPlaceholder(passwordField, "Digite sua senha");
        

        // Adicionar ao frame
        frame.add(label1);
        frame.add(xxlogin);
        frame.add(xxPass);
        frame.add(xxemail);
        frame.add(navbarlogin);
        frame.add(navbaremail);
        frame.add(passwordField);
        frame.add(buttonback);
        frame.add(labelGif);
        frame.add(xxconfirmanpass);

        // Estilização (Styling)
        label1.setFont(new Font("Arial", Font.BOLD, 27));
        xxlogin.setFont(new Font("Arial", Font.BOLD, 15));
        xxemail.setFont(new Font("Arial", Font.BOLD, 15));
        xxPass.setFont(new Font("Arial", Font.BOLD, 15));
        xxconfirmanpass.setFont(new Font("Arial", Font.BOLD, 15));
        

        // Posicionamento
        label1.setBounds(100, 20, 350, 40);
        xxlogin.setBounds(20, 70, 100, 40);
        xxemail.setBounds(20, 101, 100, 40);
        xxPass.setBounds(20, 129, 100, 40);
        navbarlogin.setBounds(100, 75, 200, 25);
        navbaremail.setBounds(70, 109, 230, 25);
        passwordField.setBounds(100, 139, 200, 25);
        buttonback.setBounds(0, 0, 68, 20);
        labelGif.setBounds(265, 16, 324, 427);
        xxconfirmanpass.setBounds(20, 163, 170, 40);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    // Alterado para JTextField para combinar com campos de entrada curtos
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