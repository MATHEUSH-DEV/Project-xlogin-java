import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField; // Use JTextField em vez de TextArea para campos de uma linha

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
        JLabel xxemail = new JLabel("Email: ");

        JTextField navbarlogin = new JTextField(); 
        JTextField navbaremail = new JTextField();
        
        
        // Placeholder (Common practice for UI/UX)
        configurarPlaceholder(navbarlogin, "Digite seu username");

        // Adicionar ao frame
        frame.add(label1);
        frame.add(xxlogin);
        frame.add(xxPass);
        frame.add(xxemail);
        frame.add(navbarlogin);
        frame.add(navbaremail);

        // Estilização (Styling)
        label1.setFont(new Font("Arial", Font.BOLD, 27));
        xxlogin.setFont(new Font("Arial", Font.BOLD, 15));
        xxemail.setFont(new Font("Arial", Font.BOLD, 15));
        xxPass.setFont(new Font("Arial", Font.BOLD, 15));

        // Posicionamento
        label1.setBounds(100, 20, 350, 40);
        xxlogin.setBounds(20, 70, 100, 40);
        xxemail.setBounds(20, 101, 100, 40);
        xxPass.setBounds(20, 129, 100, 40);
        navbarlogin.setBounds(100, 75, 200, 25);
        navbaremail.setBounds(70, 109, 225, 25);

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