package liman.ui.dialog;

import liman.model.Blok;
import liman.model.Konteyner;
import liman.model.Tir;
import liman.servis.TasimaServisi;
import liman.veri.LimanVeri;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TirKapiDialog extends JDialog {

    private LimanVeri veri = LimanVeri.getInstance();
    private TasimaServisi tasimaServisi = new TasimaServisi();

    private JComboBox<Konteyner> konteynerSecimi;
    private JComboBox<Tir> tirSecimi;
    private JTextArea logAlani;

    public TirKapiDialog(Frame parent) {
        super(parent, "Kapıya Tır Getir", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        arayuzuOlustur();
    }

    private void arayuzuOlustur() {
        JPanel ustPanel = new JPanel();
        ustPanel.setLayout(new BoxLayout(ustPanel, BoxLayout.Y_AXIS));

        JPanel atamaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atamaPanel.setBorder(BorderFactory.createTitledBorder("1. Tırı Konteynere Ata"));

        konteynerSecimi = new JComboBox<>();
        sahadakiKonteynerleriYukle();

        tirSecimi = new JComboBox<>();
        bosTirlariYukle();

        JButton ataBtn = new JButton("Tıra Ata");

        atamaPanel.add(new JLabel("Sahadaki Konteyner:"));
        atamaPanel.add(konteynerSecimi);
        atamaPanel.add(new JLabel("Boş Tır:"));
        atamaPanel.add(tirSecimi);
        atamaPanel.add(ataBtn);

        // --- Gate Çıkış Bölümü ---
        JPanel cikisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cikisPanel.setBorder(BorderFactory.createTitledBorder("2. Gate Çıkışını Onayla"));

        JComboBox<Tir> gorevliTirSecimi = new JComboBox<>();
        gorevliTirlariYukle(gorevliTirSecimi);
        JButton cikisOnaylaBtn = new JButton("Gate Çıkışını Onayla");

        cikisPanel.add(new JLabel("Görevli Tır:"));
        cikisPanel.add(gorevliTirSecimi);
        cikisPanel.add(cikisOnaylaBtn);

        ustPanel.add(atamaPanel);
        ustPanel.add(cikisPanel);
        add(ustPanel, BorderLayout.NORTH);

        logAlani = new JTextArea();
        logAlani.setEditable(false);
        logAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logAlani.setBorder(BorderFactory.createTitledBorder("İşlem Günlüğü"));
        add(new JScrollPane(logAlani), BorderLayout.CENTER);

        ataBtn.addActionListener(e -> {
            Konteyner k = (Konteyner) konteynerSecimi.getSelectedItem();
            Tir t = (Tir) tirSecimi.getSelectedItem();

            if (k == null || t == null) {
                JOptionPane.showMessageDialog(this, "Sahada konteyner veya boş tır bulunamadı.");
                return;
            }

            boolean sonuc = tasimaServisi.konteyneriTiraAta(k, t);
            if (sonuc) {
                veri.logEkle("Gate", t.getPlaka() + " plakalı tır " + k.getKonteynerId() + " konteynerine atandı.");
                logYaz(t.getPlaka() + " -> " + k.getKonteynerId() + " ataması yapıldı.");

                sahadakiKonteynerleriYukle();
                bosTirlariYukle();
                gorevliTirlariYukle(gorevliTirSecimi);
            } else {
                logYaz("HATA: Atama başarısız oldu.");
            }
        });

        cikisOnaylaBtn.addActionListener(e -> {
            Tir t = (Tir) gorevliTirSecimi.getSelectedItem();
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Görevli tır bulunamadı.");
                return;
            }

            Konteyner k = konteyneriBul(t.getTasidigiKonteynerId());
            if (k == null) {
                return;
            }

            boolean sonuc = tasimaServisi.gateCikisiOnayla(k, t);
            if (sonuc) {
                veri.logEkle("Gate", t.getPlaka() + " gate'ten çıkış yaptı. " + k.getKonteynerId() + " limandan ayrıldı.");

                for (Blok b : veri.getBloklar()) {
                    b.getIcindekiKonteynerler().remove(k);
                }

                logYaz(t.getPlaka() + " gate çıkışı onaylandı. " + k.getKonteynerId() + " limandan ayrıldı.");
                gorevliTirlariYukle(gorevliTirSecimi);
            }
        });
    }

    private void sahadakiKonteynerleriYukle() {
        konteynerSecimi.removeAllItems();
        for (Blok b : veri.getBloklar()) {
            for (Konteyner k : b.getIcindekiKonteynerler()) {
                if (k.getDurum() == Konteyner.Durum.SAHADA) {
                    konteynerSecimi.addItem(k);
                }
            }
        }
    }

    private void bosTirlariYukle() {
        tirSecimi.removeAllItems();
        for (Tir t : veri.getTirlar()) {
            if (t.getDurum() == Tir.Durum.BOS) {
                tirSecimi.addItem(t);
            }
        }
    }

    private void gorevliTirlariYukle(JComboBox<Tir> kutu) {
        kutu.removeAllItems();
        for (Tir t : veri.getTirlar()) {
            if (t.getDurum() == Tir.Durum.GOREVLI) {
                kutu.addItem(t);
            }
        }
    }

    private Konteyner konteyneriBul(String id) {
        for (Blok b : veri.getBloklar()) {
            for (Konteyner k : b.getIcindekiKonteynerler()) {
                if (k.getKonteynerId().equals(id)) {
                    return k;
                }
            }
        }
        return null;
    }

    private void logYaz(String mesaj) {
        logAlani.append(mesaj + "\n");
        logAlani.setCaretPosition(logAlani.getDocument().getLength());
    }
}
