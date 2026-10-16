package liman.ui;

import javax.swing.*;
import java.awt.*;

public class GirisEkrani extends JFrame {

    private JTextField kullaniciAdiAlani;
    private JPasswordField sifreAlani;

    public GirisEkrani() {
        setTitle("Liman Yönetim Sistemi - Giriş");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridLayout(1, 2));

        JPanel solPanel = new JPanel();
        solPanel.setBackground(new Color(44, 62, 80));
        JLabel baslik = new JLabel("<html><center>LİMAN<br>YÖNETİM<br>SİSTEMİ</center></html>");
        baslik.setForeground(Color.WHITE);
        baslik.setFont(new Font("SansSerif", Font.BOLD, 24));
        baslik.setHorizontalAlignment(SwingConstants.CENTER);
        solPanel.setLayout(new BorderLayout());
        solPanel.add(baslik, BorderLayout.CENTER);
        add(solPanel);

        JPanel sagPanel = new JPanel();
        sagPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        sagPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1;
        kullaniciAdiAlani = new JTextField(15);
        sagPanel.add(kullaniciAdiAlani, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        sagPanel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1;
        sifreAlani = new JPasswordField(15);
        sagPanel.add(sifreAlani, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton girisBtn = new JButton("Giriş Yap");
        sagPanel.add(girisBtn, gbc);

        add(sagPanel);

        girisBtn.addActionListener(e -> girisYap());

        sifreAlani.addActionListener(e -> girisYap());
    }

    private void girisYap() {
        String kullanici = kullaniciAdiAlani.getText().trim();
        String sifre = new String(sifreAlani.getPassword());

        if (kullanici.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kullanıcı adı ve şifre boş olamaz.",
                    "Giriş Hatası", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean dogru = liman.veri.LimanVeri.getInstance().girisDogrula(kullanici, sifre);

        if (!dogru) {
            JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre hatalı.",
                    "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
            sifreAlani.setText("");
            return;
        }

        new KontrolMerkezi(kullanici).setVisible(true);
        this.dispose();
    }
}
