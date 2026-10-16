package liman.ui.dialog;

import liman.model.Blok;
import liman.model.Rihtim;
import liman.veri.LimanVeri;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class SistemAyarlariDialog extends JDialog {

    private LimanVeri veri = LimanVeri.getInstance();

    public SistemAyarlariDialog(Frame parent) {
        super(parent, "Sistem Ayarları", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Rıhtım Yönetimi", rihtimSekmesiOlustur());
        sekmeler.addTab("Saha (Blok) Yönetimi", sahaSekmesiOlustur());
        sekmeler.addTab("Kullanıcı Ayarları", kullaniciSekmesiOlustur());

        add(sekmeler, BorderLayout.CENTER);
    }

    private RihtimAyarModel rihtimModel;

    private JPanel rihtimSekmesiOlustur() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        rihtimModel = new RihtimAyarModel(veri.getRihtimlar());
        JTable tablo = new JTable(rihtimModel);
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);

        JPanel butonPanel = new JPanel();
        JButton ekleBtn = new JButton("Yeni Rıhtım Ekle");
        JButton sileBtn = new JButton("Seçili Rıhtımı Sil");
        butonPanel.add(ekleBtn);
        butonPanel.add(sileBtn);
        panel.add(butonPanel, BorderLayout.SOUTH);

        ekleBtn.addActionListener(e -> rihtimEkle());

        sileBtn.addActionListener(e -> {
            int secili = tablo.getSelectedRow();
            if (secili == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek rıhtımı seçin.");
                return;
            }
            Rihtim r = veri.getRihtimlar().get(secili);
            if (r.getDurum() != Rihtim.Durum.BOS) {
                JOptionPane.showMessageDialog(this,
                        "Bu rıhtımda şu an bir gemi var, önce gemiyi ayrılmalı.",
                        "Silinemez", JOptionPane.WARNING_MESSAGE);
                return;
            }
            veri.getRihtimlar().remove(secili);
            veri.getRihtimDAO().sil(r.getRihtimId());
            rihtimModel.yenile();
            veri.logEkle("Sistem Ayarları", r.getRihtimId() + " silindi.");
        });

        return panel;
    }

    private void rihtimEkle() {
        JTextField idAlani = new JTextField("Rıhtım " + (veri.getRihtimlar().size() + 1));
        JTextField uzunlukAlani = new JTextField("300");
        JTextField derinlikAlani = new JTextField("15");
        JTextField vincAlani = new JTextField("3");

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Rıhtım Adı:"));
        form.add(idAlani);
        form.add(new JLabel("Uzunluk (m):"));
        form.add(uzunlukAlani);
        form.add(new JLabel("Derinlik (m):"));
        form.add(derinlikAlani);
        form.add(new JLabel("Vinç Sayısı:"));
        form.add(vincAlani);

        int sonuc = JOptionPane.showConfirmDialog(this, form, "Yeni Rıhtım",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (sonuc != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double uzunluk = Double.parseDouble(uzunlukAlani.getText());
            double derinlik = Double.parseDouble(derinlikAlani.getText());
            int vinc = Integer.parseInt(vincAlani.getText());

            Rihtim yeni = new Rihtim(idAlani.getText(), uzunluk, derinlik, vinc);
            veri.getRihtimlar().add(yeni);
            veri.getRihtimDAO().kaydet(yeni);
            rihtimModel.yenile();
            veri.logEkle("Sistem Ayarları", yeni.getRihtimId() + " eklendi.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Uzunluk/derinlik/vinç sayısı geçerli bir sayı olmalı.",
                    "Geçersiz Değer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class RihtimAyarModel extends AbstractTableModel {

        private final String[] kolonlar = {"Rıhtım", "Uzunluk (m)", "Derinlik (m)", "Vinç", "Durum"};
        private java.util.List<Rihtim> liste;

        RihtimAyarModel(java.util.List<Rihtim> liste) {
            this.liste = liste;
        }

        public int getRowCount() {
            return liste.size();
        }

        public int getColumnCount() {
            return kolonlar.length;
        }

        public String getColumnName(int c) {
            return kolonlar[c];
        }

        public Object getValueAt(int row, int col) {
            Rihtim r = liste.get(row);
            switch (col) {
                case 0:
                    return r.getRihtimId();
                case 1:
                    return r.getUzunluk();
                case 2:
                    return r.getDerinlik();
                case 3:
                    return r.getVincSayisi();
                case 4:
                    return r.getDurum();
                default:
                    return "";
            }
        }

        void yenile() {
            fireTableDataChanged();
        }
    }

    private BlokAyarModel blokModel;

    private JPanel sahaSekmesiOlustur() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        blokModel = new BlokAyarModel(veri.getBloklar());
        JTable tablo = new JTable(blokModel);
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);

        JPanel butonPanel = new JPanel();
        JButton ekleBtn = new JButton("Yeni Blok Ekle");
        JButton sileBtn = new JButton("Seçili Bloğu Sil");
        butonPanel.add(ekleBtn);
        butonPanel.add(sileBtn);
        panel.add(butonPanel, BorderLayout.SOUTH);

        ekleBtn.addActionListener(e -> blokEkle());

        sileBtn.addActionListener(e -> {
            int secili = tablo.getSelectedRow();
            if (secili == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek bloğu seçin.");
                return;
            }
            Blok b = veri.getBloklar().get(secili);
            if (!b.getIcindekiKonteynerler().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Bu blokta hâlâ konteyner var, önce boşaltılmalı.",
                        "Silinemez", JOptionPane.WARNING_MESSAGE);
                return;
            }
            veri.getBloklar().remove(secili);
            veri.getBlokDAO().sil(b.getBlokId());
            blokModel.yenile();
            veri.logEkle("Sistem Ayarları", b.getBlokId() + " silindi.");
        });

        return panel;
    }

    private void blokEkle() {
        JTextField idAlani = new JTextField("Blok " + (char) ('A' + veri.getBloklar().size()));
        JComboBox<Blok.BlokTipi> tipKutusu = new JComboBox<>(Blok.BlokTipi.values());
        JTextField siraAlani = new JTextField("5");
        JTextField yiginAlani = new JTextField("5");
        JTextField katAlani = new JTextField("4");

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Blok Adı:"));
        form.add(idAlani);
        form.add(new JLabel("Blok Tipi:"));
        form.add(tipKutusu);
        form.add(new JLabel("Sıra Sayısı:"));
        form.add(siraAlani);
        form.add(new JLabel("Yığın Sayısı:"));
        form.add(yiginAlani);
        form.add(new JLabel("Kat Sayısı:"));
        form.add(katAlani);

        int sonuc = JOptionPane.showConfirmDialog(this, form, "Yeni Blok",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (sonuc != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int sira = Integer.parseInt(siraAlani.getText());
            int yigin = Integer.parseInt(yiginAlani.getText());
            int kat = Integer.parseInt(katAlani.getText());

            Blok yeni = new Blok(idAlani.getText(), (Blok.BlokTipi) tipKutusu.getSelectedItem(), sira, yigin, kat);
            veri.getBloklar().add(yeni);
            veri.getBlokDAO().kaydet(yeni);
            blokModel.yenile();
            veri.logEkle("Sistem Ayarları", yeni.getBlokId() + " eklendi.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sıra/yığın/kat sayıları geçerli bir tam sayı olmalı.",
                    "Geçersiz Değer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class BlokAyarModel extends AbstractTableModel {

        private final String[] kolonlar = {"Blok", "Tip", "Sıra", "Yığın", "Kat", "Kapasite"};
        private java.util.List<Blok> liste;

        BlokAyarModel(java.util.List<Blok> liste) {
            this.liste = liste;
        }

        public int getRowCount() {
            return liste.size();
        }

        public int getColumnCount() {
            return kolonlar.length;
        }

        public String getColumnName(int c) {
            return kolonlar[c];
        }

        public Object getValueAt(int row, int col) {
            Blok b = liste.get(row);
            switch (col) {
                case 0:
                    return b.getBlokId();
                case 1:
                    return b.getTip();
                case 2:
                    return b.getMaxSira();
                case 3:
                    return b.getMaxYigin();
                case 4:
                    return b.getMaxKat();
                case 5:
                    return b.getMaxSira() * b.getMaxYigin() * b.getMaxKat();
                default:
                    return "";
            }
        }

        void yenile() {
            fireTableDataChanged();
        }
    }

    private JPanel kullaniciSekmesiOlustur() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField yeniKullaniciAlani = new JTextField(veri.getKullaniciAdi(), 15);
        JPasswordField mevcutSifreAlani = new JPasswordField(15);
        JPasswordField yeniSifreAlani = new JPasswordField(15);
        JPasswordField yeniSifreTekrarAlani = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1;
        panel.add(yeniKullaniciAlani, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mevcut Şifre:"), gbc);
        gbc.gridx = 1;
        panel.add(mevcutSifreAlani, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Yeni Şifre:"), gbc);
        gbc.gridx = 1;
        panel.add(yeniSifreAlani, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Yeni Şifre (Tekrar):"), gbc);
        gbc.gridx = 1;
        panel.add(yeniSifreTekrarAlani, gbc);

        JButton kaydetBtn = new JButton("Değişiklikleri Kaydet");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(kaydetBtn, gbc);

        kaydetBtn.addActionListener(e -> {
            String mevcutSifre = new String(mevcutSifreAlani.getPassword());
            String yeniSifre = new String(yeniSifreAlani.getPassword());
            String yeniSifreTekrar = new String(yeniSifreTekrarAlani.getPassword());
            String yeniKullanici = yeniKullaniciAlani.getText().trim();

            if (!veri.getSifre().equals(mevcutSifre)) {
                JOptionPane.showMessageDialog(this, "Mevcut şifre hatalı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (yeniKullanici.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!yeniSifre.equals(yeniSifreTekrar)) {
                JOptionPane.showMessageDialog(this, "Yeni şifreler birbiriyle uyuşmuyor.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (yeniSifre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Yeni şifre boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            veri.kullaniciBilgileriniGuncelle(yeniKullanici, yeniSifre);
            JOptionPane.showMessageDialog(this, "Kullanıcı bilgileri güncellendi.\nBir sonraki girişte yeni bilgileri kullanın.");

            mevcutSifreAlani.setText("");
            yeniSifreAlani.setText("");
            yeniSifreTekrarAlani.setText("");
        });

        return panel;
    }
}
