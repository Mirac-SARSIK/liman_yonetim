package liman.ui.dialog;

import liman.model.Tir;
import liman.servis.OperasyonYoneticisi;
import liman.veri.LimanVeri;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KapiYonetimDialog extends JDialog {

    private final LimanVeri veri = LimanVeri.getInstance();
    private final OperasyonYoneticisi op = OperasyonYoneticisi.getInstance();

    private KuyrukTableModel kuyrukModeli;
    private JCheckBox otomatikOnayKutusu;
    private JTable tablo;

    private final Runnable guncelleyici = this::arayuzuGuncelle;

    public KapiYonetimDialog(Frame parent) {
        super(parent, "Kapı Yönetimi — Tır Trafiği", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(720, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        arayuzuOlustur();
        op.kapiGuncelleyiciEkle(guncelleyici);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                op.kapiGuncelleyiciSil(guncelleyici);
                // Dialog kapanınca otomatik onay durur
                otomatikOnayKutusu.setSelected(false);
                op.setOtomatikOnay(false);
            }
        });
    }

    private void arayuzuOlustur() {
        JPanel ustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otomatikOnayKutusu = new JCheckBox("Gelen tüm tırları otomatik onayla");
        otomatikOnayKutusu.setSelected(op.isOtomatikOnay());
        ustPanel.add(otomatikOnayKutusu);
        add(ustPanel, BorderLayout.NORTH);

        kuyrukModeli = new KuyrukTableModel(veri.getTirlar());
        tablo = new JTable(kuyrukModeli);
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        JPanel altPanel = new JPanel();
        JButton onaylaBtn = new JButton("Seçili Tırı İçeri Al");
        JButton reddetBtn = new JButton("Seçili Tırı Reddet");
        altPanel.add(onaylaBtn);
        altPanel.add(reddetBtn);
        add(altPanel, BorderLayout.SOUTH);

        otomatikOnayKutusu.addActionListener(e
                -> op.setOtomatikOnay(otomatikOnayKutusu.isSelected()));

        onaylaBtn.addActionListener(e -> {
            int secili = tablo.getSelectedRow();
            if (secili == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir tır seçin.");
                return;
            }
            List<Tir> aktif = getAktifTirlar();
            if (secili < aktif.size()) {
                op.tirOnayla(aktif.get(secili));
            }
        });

        reddetBtn.addActionListener(e -> {
            int secili = tablo.getSelectedRow();
            if (secili == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir tır seçin.");
                return;
            }
            List<Tir> aktif = getAktifTirlar();
            if (secili < aktif.size()) {
                Tir t = aktif.get(secili);
                if (t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR) {
                    veri.getTirlar().remove(t);
                    veri.logEkle("Gate", t.getPlaka() + " reddedildi ve ayrıldı.");
                    arayuzuGuncelle();
                }
            }
        });
    }

    private void arayuzuGuncelle() {
        kuyrukModeli.guncelle(veri.getTirlar());
    }

    private List<Tir> getAktifTirlar() {
        return veri.getTirlar().stream()
                .filter(t -> t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR
                || t.getDurum() == Tir.Durum.GOREVLI)
                .collect(Collectors.toList());
    }

    private static class KuyrukTableModel extends AbstractTableModel {

        private final String[] kolonlar = {"Tır Plakası", "Şoför", "Konteyner", "Durum", "Kalan Süre"};
        private List<Tir> gosterilen;

        KuyrukTableModel(List<Tir> tumListe) {
            gosterilen = filtrele(tumListe);
        }

        void guncelle(List<Tir> tumListe) {
            gosterilen = filtrele(tumListe);
            fireTableDataChanged();
        }

        private List<Tir> filtrele(List<Tir> liste) {
            return liste.stream()
                    .filter(t -> t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR
                    || t.getDurum() == Tir.Durum.GOREVLI)
                    .collect(Collectors.toList());
        }

        public int getRowCount() {
            return gosterilen.size();
        }

        public int getColumnCount() {
            return kolonlar.length;
        }

        public String getColumnName(int c) {
            return kolonlar[c];
        }

        public Object getValueAt(int row, int col) {
            if (row >= gosterilen.size()) {
                return "";
            }
            Tir t = gosterilen.get(row);
            switch (col) {
                case 0:
                    return t.getPlaka();
                case 1:
                    return t.getSoforAdi();
                case 2:
                    return t.getTasidigiKonteynerId() != null ? t.getTasidigiKonteynerId() : "-";
                case 3:
                    return t.getDurum();
                case 4:
                    return t.getDurum() == Tir.Durum.GOREVLI
                            ? t.getKalanIslemSaniyesi() + " sn" : "Onay bekleniyor";
                default:
                    return "";
            }
        }
    }
}
