package liman.ui;

import liman.model.*;
import liman.servis.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnaEkran extends JFrame {

    private List<Gemi> gemiler = new ArrayList<>();
    private List<Rihtim> rihtimlar = new ArrayList<>();
    private List<Blok> bloklar = new ArrayList<>();
    private List<Tir> tirlar = new ArrayList<>();

    private RihtimAtamaServisi rihtimAtamaServisi = new RihtimAtamaServisi();
    private BosaltmaServisi bosaltmaServisi = new BosaltmaServisi();
    private TasimaServisi tasimaServisi = new TasimaServisi();

    private JTextArea logAlani;
    private JComboBox<Gemi> gemiSecimi;
    private JComboBox<Rihtim> rihtimSecimi;

    public AnaEkran() {
        baslangicVerileriniYukle();
        arayuzuOlustur();
    }

    private void baslangicVerileriniYukle() {
        rihtimlar.add(new Rihtim("RIHTIM-1", 300, 15, 3));
        rihtimlar.add(new Rihtim("RIHTIM-2", 250, 12, 2));

        bloklar.add(new Blok("BLOK-ITH-1", Blok.BlokTipi.ITHALAT, 5, 5, 4));
        bloklar.add(new Blok("BLOK-IHR-1", Blok.BlokTipi.IHRACAT, 5, 5, 4));
        bloklar.add(new Blok("BLOK-REEFER-1", Blok.BlokTipi.REEFER, 3, 3, 2));
        bloklar.add(new Blok("BLOK-BOS-1", Blok.BlokTipi.BOS_KONTEYNER, 4, 4, 3));

    }

    private void arayuzuOlustur() {
        setTitle("Liman Yönetim Sistemi");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel ustPanel = new JPanel();
        ustPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton gemiOlusturBtn = new JButton("Gemi Oluştur");
        JButton rihtimaAtaBtn = new JButton("Seçili Gemiyi Rıhtıma Ata");
        JButton bosaltBtn = new JButton("Seçili Gemiyi Boşalt");

        gemiSecimi = new JComboBox<>();
        rihtimSecimi = new JComboBox<>();
        for (Rihtim r : rihtimlar) {
            rihtimSecimi.addItem(r);
        }

        ustPanel.add(gemiOlusturBtn);
        ustPanel.add(new JLabel("Gemi:"));
        ustPanel.add(gemiSecimi);
        ustPanel.add(new JLabel("Rıhtım:"));
        ustPanel.add(rihtimSecimi);
        ustPanel.add(rihtimaAtaBtn);
        ustPanel.add(bosaltBtn);

        add(ustPanel, BorderLayout.NORTH);

        logAlani = new JTextArea();
        logAlani.setEditable(false);
        logAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logAlani);
        add(scrollPane, BorderLayout.CENTER);

        gemiOlusturBtn.addActionListener(e -> {
            Gemi yeniGemi = RastgeleVeriUretici.rastgeleGemiUret();
            gemiler.add(yeniGemi);
            gemiSecimi.addItem(yeniGemi);
            logYaz("Yeni gemi oluşturuldu: " + yeniGemi + " | Konteyner sayısı: " + yeniGemi.getKonteynerListesi().size());
        });

        rihtimaAtaBtn.addActionListener(e -> {
            Gemi secilenGemi = (Gemi) gemiSecimi.getSelectedItem();
            if (secilenGemi == null) {
                logYaz("UYARI: Önce bir gemi seçmelisiniz.");
                return;
            }
            boolean sonuc = rihtimAtamaServisi.gemiyiRihtimaAta(secilenGemi, rihtimlar);
            logYaz(sonuc
                    ? secilenGemi.getGemiAdi() + " rıhtıma yanaştı -> " + secilenGemi.getBaglıRihtimId()
                    : secilenGemi.getGemiAdi() + " için uygun rıhtım bulunamadı.");
        });

        bosaltBtn.addActionListener(e -> {
            Gemi secilenGemi = (Gemi) gemiSecimi.getSelectedItem();
            if (secilenGemi == null) {
                logYaz("UYARI: Önce bir gemi seçmelisiniz.");
                return;
            }
            if (secilenGemi.getDurum() != Gemi.Durum.YANASTI) {
                logYaz("UYARI: Gemi henüz rıhtıma yanaşmadı, boşaltma yapılamaz.");
                return;
            }
            bosaltmaServisi.gemiyiBosalt(secilenGemi, bloklar);
            logYaz(secilenGemi.getGemiAdi() + " boşaltması tamamlandı. Detaylar konsolda.");
        });
    }

    private void logYaz(String mesaj) {
        logAlani.append(mesaj + "\n");
        logAlani.setCaretPosition(logAlani.getDocument().getLength());
    }
}
