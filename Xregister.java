import javax.swing.JFrame;
import javax.swing.JLabel;

public class Xregister {
    
    public void abrirTela() {
        JFrame frame = new JFrame("Kronus Register v1.0");
        frame.setSize(550, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE fecha apenas esta janela
        frame.setLayout(null);
        frame.setResizable(false);

        // elementos
        JLabel label1 = new JLabel("Kronus REGISTRO v1.0");
        JLabel xxlogin = new JLabel("username: ");
        JLabel xxPass = new JLabel("Password: ");
        JLabel xxemail = new JLabel("Email: ");


        // Adicione seus componentes do registro aqui...

        // adicionar
        frame.add(label1);
        frame.add(xxlogin);
        frame.add(xxPass);
        frame.add (xxemail);

        // tamanho JLabel
        label1.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 27));

        // tamanho / posicionamento
        label1.setBounds(100, 20, 350, 40);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
}