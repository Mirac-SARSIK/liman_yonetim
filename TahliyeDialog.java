package liman.ui.dialog;

import liman.model.*;
import liman.servis.OperasyonYoneticisi;
import liman.veri.LimanVeri;

import javax.swing.*;
import java.awt.*;
import liman.servis.LimanSaati;

public class TahliyeDialog extends JDialog {

    private final LimanVeri veri = LimanVeri.getInstance();
    private final OperasyonYoneticisi op = OperasyonYoneticisi.getInstance();

    private JComboBox<Gemi> gemiSecimi;
    private JProgressBar ilerlemeBar;
    private JLabel durumEtiketi;
    private JTextArea logAlani;
    private JButton tahliyeBtn;
    private JButton yukBindirBtn;
    private JButton ayrilBtn;
    private int sonGosterilen = 0;

    private final Runnable guncelleyici = this::arayuzuGuncelle;

    public TahliyeDialog(Frame parent) {
        super(parent, "Tahliye & Yük Yönetimi", true);
        this.parentFrame = parent;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(670, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        arayuzuOlustur();
        op.tahliyeGuncelleyiciEkle(guncelleyici);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                op.tahliyeGuncelleyiciSil(guncelleyici);
            }
        });

        // Tahliye zaten devam ediyorsa durumu hemen göster
        if (op.isTahliyeAktif()) {
            tahliyeBtn.setEnabled(false);
            sonGosterilen = veri.getOlayGunlugu().size();
            arayuzuGuncelle();
        }
    }
    private final Frame parentFrame;

    private void arayuzuOlustur() {

        JPanel ustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gemiSecimi = new JComboBox<>();
        for (Gemi g : veri.getGemiler()) {
            if (g.getDurum() == Gemi.Durum.YANASTI
                    || g.getDurum() == Gemi.Durum.TAHLIYE_TAMAMLANDI
                    || g.getDurum() == Gemi.Durum.BOSALTILIYOR) {
                gemiSecimi.addItem(g);
            }
        }

        tahliyeBtn = new JButton("Tahliyeyi Başlat");
        yukBindirBtn = new JButton("Sahadan Yük Bindirme Yap");
        ayrilBtn = new JButton("Gemiyi Rıhtımdan Ayır");
        yukBindirBtn.setEnabled(false);
        ayrilBtn.setEnabled(false);

        JButton konteynerBtn = new JButton("Gemideki Konteynerleri Gör");

        ustPanel.add(new JLabel("Gemi:"));
        ustPanel.add(gemiSecimi);
        ustPanel.add(tahliyeBtn);
        ustPanel.add(konteynerBtn);
        add(ustPanel, BorderLayout.NORTH);

        JPanel ortaPanel = new JPanel(new BorderLayout(5, 5));
        durumEtiketi = new JLabel("Bekleniyor...", SwingConstants.CENTER);
        durumEtiketi.setFont(new Font("SansSerif", Font.BOLD, 12));
        ilerlemeBar = new JProgressBar(0, 100);
        ilerlemeBar.setStringPainted(true);

        JPanel ilerlemePaneli = new JPanel(new BorderLayout());
        ilerlemePaneli.add(durumEtiketi, BorderLayout.NORTH);
        ilerlemePaneli.add(ilerlemeBar, BorderLayout.SOUTH);

        logAlani = new JTextArea();
        logAlani.setEditable(false);
        logAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ortaPanel.add(ilerlemePaneli, BorderLayout.NORTH);
        ortaPanel.add(new JScrollPane(logAlani), BorderLayout.CENTER);
        add(ortaPanel, BorderLayout.CENTER);

        JPanel altPanel = new JPanel();
        altPanel.add(yukBindirBtn);
        altPanel.add(ayrilBtn);
        add(altPanel, BorderLayout.SOUTH);

        if (gemiSecimi.getItemCount() == 0) {
            durumEtiketi.setText("Rıhtımda işlem yapılabilir gemi yok.");
            tahliyeBtn.setEnabled(false);
        }

        tahliyeBtn.addActionListener(e -> {
            Gemi secilen = (Gemi) gemiSecimi.getSelectedItem();
            if (secilen == null) {
                return;
            }
            if (!op.tahliyeBaslat(secilen)) {
                JOptionPane.showMessageDialog(this, "Zaten aktif bir tahliye işlemi var.");
                return;
            }
            sonGosterilen = veri.getOlayGunlugu().size() - 1;
            tahliyeBtn.setEnabled(false);
        });

        konteynerBtn.addActionListener(e -> {
            Gemi secilen = (Gemi) gemiSecimi.getSelectedItem();
            if (secilen == null) {
                return;
            }

            setVisible(false);

            GemiKonteynerDialog gkd = new GemiKonteynerDialog(parentFrame, secilen);

            gkd.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent we) {
                    setVisible(true);
                }
            });
            gkd.setVisible(true);
        });

        yukBindirBtn.addActionListener(e -> {
            Gemi secilen = op.getTahliyeGemi();
            if (secilen == null) {
                secilen = (Gemi) gemiSecimi.getSelectedItem();
            }
            if (secilen == null) {
                return;
            }

            int yuklenen = op.yukBindirme(secilen);
            if (yuklenen == 0) {
                JOptionPane.showMessageDialog(this,
                        "Sahada yüklenecek ihracat konteyneri bulunamadı.\n(İhracat bloğu boş)");
            } else {
                JOptionPane.showMessageDialog(this, yuklenen + " ihracat konteyneri gemiye yüklendi.");
                yukBindirBtn.setEnabled(false);
            }
        });

        ayrilBtn.addActionListener(e -> {
            op.gemiyiRihtimindanAyir();
            JOptionPane.showMessageDialog(this, "Gemi rıhtımdan ayrıldı. Rıhtım tekrar kullanılabilir.");
            dispose();
        });
    }

    private void arayuzuGuncelle() {
        if (op.isTahliyeAktif()) {
            int toplam = op.getTahliyeToplamKonteyner();
            int islenen = op.getTahliyeIslenenSayisi();
            int yuzde = toplam > 0 ? (int) ((islenen / (double) toplam) * 100) : 0;
            ilerlemeBar.setValue(yuzde);
            ilerlemeBar.setString(islenen + " / " + toplam);

            if (op.isTahliiyeDuraksatildi()) {
                durumEtiketi.setText("⚠ DURAKSATILDI — Saha dolu, tırlar boşaltana kadar bekleniyor...");
                durumEtiketi.setForeground(Color.RED);
            } else {

                long gecen = LimanSaati.getInstance().getGecenSaniye();
                long kalanSn = 5 - (gecen % 5);
                durumEtiketi.setText("▶ Tahliye devam ediyor...  " + islenen + "/" + toplam
                        + "  |  Sonraki batch: " + kalanSn + " sn");
                durumEtiketi.setForeground(new Color(46, 130, 46));
            }

        } else if (op.getTahliyeGemi() != null
                && op.getTahliyeGemi().getDurum() == Gemi.Durum.TAHLIYE_TAMAMLANDI) {
            ilerlemeBar.setValue(100);
            ilerlemeBar.setString("Tamamlandı");
            durumEtiketi.setText("✓ Tahliye tamamlandı! Yük bindirme yapabilir veya rıhtımdan ayırabilirsiniz.");
            durumEtiketi.setForeground(new Color(46, 204, 113));
            yukBindirBtn.setEnabled(true);
            ayrilBtn.setEnabled(true);
        }

        java.util.List<String> gunluk = veri.getOlayGunlugu();
        while (sonGosterilen < gunluk.size()) {
            logAlani.append(gunluk.get(sonGosterilen) + "\n");
            sonGosterilen++;
        }
        if (logAlani.getDocument().getLength() > 0) {
            logAlani.setCaretPosition(logAlani.getDocument().getLength());
        }
    }

    private String islened(int islenen, int toplam) {
        return islenen + " / " + toplam;
    }
}
