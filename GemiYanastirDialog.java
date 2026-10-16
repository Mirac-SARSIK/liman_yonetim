package liman.ui.dialog;

import liman.model.Gemi;
import liman.model.Konteyner;
import liman.model.Rihtim;
import liman.servis.RastgeleVeriUretici;
import liman.veri.LimanVeri;
import liman.util.IdUretici;

import javax.swing.*;
import java.awt.*;

public class GemiYanastirDialog extends JDialog {

    private final LimanVeri veri = LimanVeri.getInstance();
    private Gemi olusturulanGemi;

    private JTextArea gemiBilgiAlani;
    private JComboBox<Rihtim> rihtimSecimi;
    private JButton onaylaBtn;

    private JTextField adAlani, uzunlukAlani, genislikAlani,
            suCekimiAlani, kapasiteAlani, konteynerSayisiAlani;

    public GemiYanastirDialog(Frame parent) {
        super(parent, "Yeni Gemi Yanaştır", true);
        setSize(560, 540);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Rastgele Üretim", rastgeleSekmesiOlustur());
        sekmeler.addTab("Manuel Giriş", manuelSekmesiOlustur());
        add(sekmeler, BorderLayout.NORTH);

        gemiBilgiAlani = new JTextArea();
        gemiBilgiAlani.setEditable(false);
        gemiBilgiAlani.setFont(new Font("Monospaced", Font.PLAIN, 13));
        gemiBilgiAlani.setBorder(BorderFactory.createTitledBorder("Gemi Bilgileri"));
        add(new JScrollPane(gemiBilgiAlani), BorderLayout.CENTER);

        add(altPanelOlustur(), BorderLayout.SOUTH);
    }

    private JPanel rastgeleSekmesiOlustur() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton uretBtn = new JButton("Rastgele Gemi Üret");
        panel.add(uretBtn);
        uretBtn.addActionListener(e -> {
            olusturulanGemi = RastgeleVeriUretici.rastgeleGemiUret();
            gemiyiHazirla();
        });
        return panel;
    }

    private JPanel manuelSekmesiOlustur() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        adAlani = new JTextField("Yeni Gemi", 15);
        uzunlukAlani = new JTextField("250", 15);
        genislikAlani = new JTextField("32", 15);
        suCekimiAlani = new JTextField("12", 15);
        kapasiteAlani = new JTextField("5000", 15);
        konteynerSayisiAlani = new JTextField("60", 15);

        String[][] satirlar = {
            {"Gemi Adı:", "ad"},
            {"Uzunluk (m):", "uzunluk"},
            {"Genişlik (m):", "genislik"},
            {"Su Çekimi (m):", "suCekimi"},
            {"Kapasite (TEU):", "kapasite"},
            {"Yüklü Konteyner Sayısı:", "konteynerSayisi"}
        };
        JTextField[] alanlar = {adAlani, uzunlukAlani, genislikAlani,
            suCekimiAlani, kapasiteAlani, konteynerSayisiAlani};

        for (int i = 0; i < satirlar.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(satirlar[i][0]), gbc);
            gbc.gridx = 1;
            panel.add(alanlar[i], gbc);
        }

        JButton olusturBtn = new JButton("Gemiyi Oluştur");
        gbc.gridx = 1;
        gbc.gridy = satirlar.length;
        panel.add(olusturBtn, gbc);

        olusturBtn.addActionListener(e -> manuelGemiOlustur());
        return panel;
    }

    private void manuelGemiOlustur() {
        String ad = adAlani.getText().trim();
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gemi adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double uzunluk = Double.parseDouble(uzunlukAlani.getText().trim());
            double genislik = Double.parseDouble(genislikAlani.getText().trim());
            double suCekimi = Double.parseDouble(suCekimiAlani.getText().trim());
            int kapasite = Integer.parseInt(kapasiteAlani.getText().trim());
            int kSayisi = Integer.parseInt(konteynerSayisiAlani.getText().trim());

            if (uzunluk <= 0 || genislik <= 0 || suCekimi <= 0 || kapasite <= 0) {
                JOptionPane.showMessageDialog(this, "Değerler pozitif olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (kSayisi > kapasite) {
                JOptionPane.showMessageDialog(this,
                        "Konteyner sayısı kapasite (" + kapasite + ") değerini aşamaz.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = IdUretici.gemiIdUret();
            olusturulanGemi = new Gemi(id, ad, uzunluk, genislik, suCekimi, kapasite);
            for (int i = 0; i < kSayisi; i++) {
                olusturulanGemi.konteynerEkle(RastgeleVeriUretici.rastgeleKonteynerUret());
            }
            gemiyiHazirla();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Sayısal alanlara geçerli değer girin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gemiyiHazirla() {

        veri.getGemiler().add(olusturulanGemi);

        veri.getGemiDAO().kaydet(olusturulanGemi);
        veri.getKonteynerDAO().tumunuKaydet(olusturulanGemi.getKonteynerListesi());

        StringBuilder sb = new StringBuilder();
        sb.append("Gemi ID          : ").append(olusturulanGemi.getGemiId()).append("\n");
        sb.append("Gemi Adı         : ").append(olusturulanGemi.getGemiAdi()).append("\n");
        sb.append("Uzunluk          : ").append(olusturulanGemi.getUzunluk()).append(" m\n");
        sb.append("Genişlik         : ").append(olusturulanGemi.getGenislik()).append(" m\n");
        sb.append("Su Çekimi (Draft): ").append(olusturulanGemi.getSuCekimi()).append(" m\n");
        sb.append("Kapasite         : ").append(olusturulanGemi.getKapasiteTEU()).append(" TEU\n");
        sb.append("Konteyner Sayısı : ").append(olusturulanGemi.getKonteynerListesi().size()).append("\n");
        sb.append("Durum            : ").append(olusturulanGemi.getDurum()).append("\n");
        gemiBilgiAlani.setText(sb.toString());

        veri.logEkle("Sistem", olusturulanGemi.getGemiAdi()
                + " (" + olusturulanGemi.getGemiId() + ") sisteme kaydedildi.");

        // Dropdown'u güncelle ve butonu aktif et
        rihtimSecimiYenile();
        onaylaBtn.setEnabled(rihtimSecimi.getItemCount() > 0);

        if (rihtimSecimi.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Şu an uygun (boş) rıhtım yok.\n"
                    + "Sistem Ayarları'ndan yeni rıhtım ekleyin veya mevcut rıhtımı boşaltın.",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel altPanelOlustur() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        rihtimSecimi = new JComboBox<>();
        rihtimSecimiYenile();

        onaylaBtn = new JButton("Rıhtıma Yanaştır");
        onaylaBtn.setEnabled(false);

        panel.add(new JLabel("Rıhtım Seç:"));
        panel.add(rihtimSecimi);
        panel.add(onaylaBtn);

        onaylaBtn.addActionListener(e -> rihtimaYanastir());
        return panel;
    }

    private void rihtimSecimiYenile() {
        rihtimSecimi.removeAllItems();
        for (Rihtim r : veri.getRihtimlar()) {
            if (r.getDurum() == Rihtim.Durum.BOS) {
                rihtimSecimi.addItem(r);
            }
        }
    }

    private void rihtimaYanastir() {
        if (olusturulanGemi == null) {
            JOptionPane.showMessageDialog(this,
                    "Önce bir gemi oluşturmalısınız.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Rihtim secilen = (Rihtim) rihtimSecimi.getSelectedItem();
        if (secilen == null) {
            JOptionPane.showMessageDialog(this,
                    "Seçili rıhtım bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!secilen.gemiyeUygunMu(olusturulanGemi)) {
            JOptionPane.showMessageDialog(this,
                    "Bu gemi seçilen rıhtıma uygun değil!\n\n"
                    + "Gemi uzunluğu : " + olusturulanGemi.getUzunluk() + " m  "
                    + "(Rıhtım maks: " + secilen.getUzunluk() + " m)\n"
                    + "Gemi draftı   : " + olusturulanGemi.getSuCekimi() + " m  "
                    + "(Rıhtım derinlik: " + secilen.getDerinlik() + " m)",
                    "Uygunsuz Atama", JOptionPane.WARNING_MESSAGE);
            return;
        }

        secilen.setDurum(Rihtim.Durum.MESGUL);
        secilen.setBaglıGemiId(olusturulanGemi.getGemiId());
        olusturulanGemi.setBaglıRihtimId(secilen.getRihtimId());
        olusturulanGemi.setDurum(Gemi.Durum.YANASTI);

        veri.getRihtimDAO().kaydet(secilen);
        veri.getGemiDAO().kaydet(olusturulanGemi);

        veri.logEkle("Operatör", olusturulanGemi.getGemiAdi()
                + " → " + secilen.getRihtimId() + " rıhtımına yanaştırıldı.");

        JOptionPane.showMessageDialog(this,
                olusturulanGemi.getGemiAdi() + " başarıyla yanaştırıldı.\n"
                + "Rıhtım: " + secilen.getRihtimId(),
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}
