import javax.swing.JFrame;
import javax.swing.JLabel;

public class Xregister {
    
    public void abrirTela() {
        JFrame frame = new JFrame("X Register");
        frame.setSize(550, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE fecha apenas esta janela
        frame.setLayout(null);
        frame.setResizable(false);

        // elementos
        JLabel label1 = new JLabel("REGISTRO v1.0");
        

        // Adicione seus componentes do registro aqui...

        // adicionar
        frame.add(label1);

        // tamanho JLabel
        label1.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 25));

        // tamanho
        label1.setBounds(80, 20, 250, 40);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
}