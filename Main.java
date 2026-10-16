package liman;

import liman.ui.GirisEkrani;
import liman.veri.LimanVeri;
import liman.veri.VeriTabaniBaglantisi;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LimanVeri.getInstance().hepsiniKaydet();
            VeriTabaniBaglantisi.baglantiKapat();
        }));

        SwingUtilities.invokeLater(() -> {
            if (!mysqlBaglantiKur()) {
                System.exit(0);
                return;
            }
            new GirisEkrani().setVisible(true);
        });
    }

    private static boolean mysqlBaglantiKur() {
        // Önce varsayılan şifreyle dene
        if (VeriTabaniBaglantisi.veritabaniKur()) {
            return true;
        }

        // Olmadıysa kullanıcıdan sor
        while (true) {
            JPanel panel = new JPanel(new GridLayout(3, 1, 5, 8));
            panel.add(new JLabel(
                "<html><b>MySQL bağlantısı kurulamadı.</b><br>" +
                "Lütfen MySQL root şifrenizi girin:</html>"
            ));
            JPasswordField sifreAlani = new JPasswordField(15);
            panel.add(sifreAlani);
            panel.add(new JLabel(
                "<html><small>MySQL çalışır durumda olmalı (Port: 3306)</small></html>"
            ));

            int secim = JOptionPane.showConfirmDialog(
                null, panel,
                "Liman Yönetim Sistemi — Veritabanı Bağlantısı",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (secim != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null,
                    "Uygulama kapatılıyor.",
                    "Çıkış", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            String sifre = new String(sifreAlani.getPassword());
            VeriTabaniBaglantisi.setSifre(sifre);

            if (VeriTabaniBaglantisi.veritabaniKur()) {
                return true;
            }

            JOptionPane.showMessageDialog(null,
                "Bağlantı kurulamadı. Şifreyi kontrol edip tekrar deneyin.",
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}