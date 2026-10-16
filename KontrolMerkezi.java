package liman.ui;

import liman.model.*;
import liman.veri.LimanVeri;
import liman.ui.tablo.*;
import liman.ui.dialog.*;
import javax.swing.*;
import java.awt.*;
import liman.servis.LimanSaati;
import liman.servis.OperasyonYoneticisi;

public class KontrolMerkezi extends JFrame {

    private LimanVeri veri = LimanVeri.getInstance();
    private RihtimTableModel rihtimModel;
    private BlokTableModel blokModel;
    private TirTableModel tirModel;

    private JTextArea logAlani;
    private String aktifKullanici;

    private JLabel tahliyeDurumEtiketi;
    private JLabel kapiDurumEtiketi;

    public KontrolMerkezi(String kullaniciAdi) {
        this.aktifKullanici = kullaniciAdi;
        LimanSaati.getInstance().baslat();
        new liman.servis.TirGelisServisi().baslat();
        OperasyonYoneticisi.getInstance().genelGuncelleyiciEkle(this::canliGuncelle);
        arayuzuOlustur();
        logAlaniniYenile();
    }

    private void arayuzuOlustur() {
        setTitle("Liman Terminal İşletim Sistemi (TOS) - Kontrol Merkezi");
        setSize(1280, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(ustBaslikOlustur(), BorderLayout.NORTH);
        add(solMenuOlustur(), BorderLayout.WEST);
        add(sagPanelOlustur(), BorderLayout.EAST);
        add(altLogOlustur(), BorderLayout.SOUTH);

        add(new LimanHaritasiPanel(), BorderLayout.CENTER);

    }

    private void canliGuncelle() {
        tablolariYenile();
        logAlaniniYenile();
        durumEtiketleriniGuncelle();
    }

    private void durumEtiketleriniGuncelle() {
        OperasyonYoneticisi op = OperasyonYoneticisi.getInstance();

        if (op.isTahliyeAktif()) {
            String dur = op.isTahliiyeDuraksatildi() ? "⏸ DURAKSATILDI" : "▶ AKTİF";
            Gemi g = op.getTahliyeGemi();
            tahliyeDurumEtiketi.setText("Tahliye: " + dur + (g != null ? " — " + g.getGemiAdi() : ""));
            tahliyeDurumEtiketi.setForeground(op.isTahliiyeDuraksatildi() ? Color.ORANGE : new Color(46, 204, 113));
        } else {
            tahliyeDurumEtiketi.setText("Tahliye: Yok");
            tahliyeDurumEtiketi.setForeground(Color.GRAY);
        }

        long bekleyen = veri.getTirlar().stream()
                .filter(t -> t.getDurum() == liman.model.Tir.Durum.KAPIDA_BEKLIYOR).count();
        kapiDurumEtiketi.setText("Kapıda: " + bekleyen + " tır bekliyor");
        kapiDurumEtiketi.setForeground(bekleyen > 0 ? new Color(241, 196, 15) : Color.GRAY);
    }

    private JPanel ustBaslikOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(31, 41, 51));
        panel.setPreferredSize(new Dimension(0, 65));

        JLabel baslik = new JLabel("  LİMAN YÖNETİM SİSTEMİ (TOS)");
        baslik.setForeground(Color.WHITE);
        baslik.setFont(new Font("SansSerif", Font.BOLD, 18));

        tahliyeDurumEtiketi = new JLabel("Tahliye: Yok");
        tahliyeDurumEtiketi.setForeground(Color.GRAY);
        tahliyeDurumEtiketi.setFont(new Font("SansSerif", Font.BOLD, 11));

        kapiDurumEtiketi = new JLabel("Kapıda: 0 tır bekliyor");
        kapiDurumEtiketi.setForeground(Color.GRAY);
        kapiDurumEtiketi.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel kullanici = new JLabel("Kullanıcı: " + aktifKullanici + "  |  ÇEVRİMİÇİ  ");
        kullanici.setForeground(new Color(46, 204, 113));
        kullanici.setFont(new Font("SansSerif", Font.BOLD, 11));

        JPanel sagPanel = new JPanel();
        sagPanel.setBackground(new Color(31, 41, 51));
        sagPanel.setLayout(new BoxLayout(sagPanel, BoxLayout.Y_AXIS));
        sagPanel.add(kullanici);
        sagPanel.add(tahliyeDurumEtiketi);
        sagPanel.add(kapiDurumEtiketi);

        panel.add(baslik, BorderLayout.WEST);
        panel.add(sagPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel solMenuOlustur() {
        JPanel disPanel = new JPanel(new BorderLayout());
        disPanel.setBorder(BorderFactory.createTitledBorder("Operasyon Kontrol"));
        disPanel.setPreferredSize(new Dimension(220, 0));

        JPanel ustButonlar = new JPanel();
        ustButonlar.setLayout(new BoxLayout(ustButonlar, BoxLayout.Y_AXIS));

        JButton gemiYanastirBtn = buton("Yeni Gemi Yanaştır");
        JButton tahliyeBtn = buton("Tahliyeyi Başlat");
        JButton tirGetirBtn = buton("Kapıya Tır Getir");
        JButton sahaYonetimBtn = buton("Saha İstif (Yard) Yönetimi");
        

        ustButonlar.add(gemiYanastirBtn);
        ustButonlar.add(Box.createVerticalStrut(10));
        ustButonlar.add(tahliyeBtn);
        ustButonlar.add(Box.createVerticalStrut(20));
        ustButonlar.add(tirGetirBtn);
        ustButonlar.add(Box.createVerticalStrut(10));
        ustButonlar.add(sahaYonetimBtn);
        ustButonlar.add(Box.createVerticalStrut(20));


        disPanel.add(ustButonlar, BorderLayout.NORTH);

        JPanel altButonlar = new JPanel();
        altButonlar.setLayout(new BoxLayout(altButonlar, BoxLayout.Y_AXIS));
        altButonlar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton ayarlarBtn = buton("Sistem Ayarları");
        altButonlar.add(ayarlarBtn);

        disPanel.add(altButonlar, BorderLayout.SOUTH);

        gemiYanastirBtn.addActionListener(e -> {
            liman.ui.dialog.GemiYanastirDialog dialog = new liman.ui.dialog.GemiYanastirDialog(this);
            dialog.setVisible(true);
            tablolariYenile();
        });

        tahliyeBtn.addActionListener(e -> {
            liman.ui.dialog.TahliyeDialog dialog = new liman.ui.dialog.TahliyeDialog(this);
            dialog.setVisible(true);
            tablolariYenile();
        });

        tirGetirBtn.addActionListener(e -> {
            liman.ui.dialog.KapiYonetimDialog dialog = new liman.ui.dialog.KapiYonetimDialog(this);
            dialog.setVisible(true);
            tablolariYenile();
        });

        sahaYonetimBtn.addActionListener(e -> {
            liman.ui.dialog.SahaYonetimDialog dialog = new liman.ui.dialog.SahaYonetimDialog(this);
            dialog.setVisible(true);
        });

        

        ayarlarBtn.addActionListener(e -> {
            liman.ui.dialog.SistemAyarlariDialog dialog = new liman.ui.dialog.SistemAyarlariDialog(this);
            dialog.setVisible(true);
            tablolariYenile();
        });

        return disPanel;
    }

    private JButton buton(String metin) {
        JButton b = new JButton(metin);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(190, 35));
        return b;
    }

    private JPanel sagPanelOlustur() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(420, 0));

        rihtimModel = new RihtimTableModel(veri.getRihtimlar());
        JTable rihtimTablosu = new JTable(rihtimModel);
        JScrollPane rihtimScroll = new JScrollPane(rihtimTablosu);
        rihtimScroll.setBorder(BorderFactory.createTitledBorder("Canlı Rıhtım İzleme"));
        rihtimScroll.setPreferredSize(new Dimension(400, 150));

        blokModel = new BlokTableModel(veri.getBloklar());
        JTable blokTablosu = new JTable(blokModel);
        JScrollPane blokScroll = new JScrollPane(blokTablosu);
        blokScroll.setBorder(BorderFactory.createTitledBorder("Saha İstif (Yard) Durumu"));
        blokScroll.setPreferredSize(new Dimension(400, 150));

        tirModel = new TirTableModel(veri.getTirlar());
        JTable tirTablosu = new JTable(tirModel);
        JScrollPane tirScroll = new JScrollPane(tirTablosu);
        tirScroll.setBorder(BorderFactory.createTitledBorder("Kapı / Tır Trafiği"));
        tirScroll.setPreferredSize(new Dimension(400, 150));

        panel.add(rihtimScroll);
        panel.add(blokScroll);
        panel.add(tirScroll);

        return panel;
    }

    private JScrollPane altLogOlustur() {
        logAlani = new JTextArea();
        logAlani.setEditable(false);
        logAlani.setBackground(Color.BLACK);
        logAlani.setForeground(new Color(46, 204, 113));
        logAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(logAlani);
        scroll.setBorder(BorderFactory.createTitledBorder("Terminal Olay Günlüğü (Logs)"));
        scroll.setPreferredSize(new Dimension(0, 150));
        return scroll;
    }

    public void tablolariYenile() {
        rihtimModel.yenile();
        blokModel.yenile();
        tirModel.yenile();
        logAlaniniYenile();
    }

    private void logAlaniniYenile() {
        logAlani.setText("");
        for (String satir : veri.getOlayGunlugu()) {
            logAlani.append(satir + "\n");
        }
        logAlani.setCaretPosition(logAlani.getDocument().getLength());
    }
}
