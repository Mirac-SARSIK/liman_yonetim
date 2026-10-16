package liman.ui.dialog;

import liman.model.Blok;
import liman.model.Konteyner;
import liman.veri.LimanVeri;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class SahaYonetimDialog extends JDialog {

    private final LimanVeri veri = LimanVeri.getInstance();
    private KonteynerTableModel tabloModeli;
    private JTable tablo;
    private JComboBox<Blok> blokSecimi;

    public SahaYonetimDialog(Frame parent) {
        super(parent, "Saha İstif (Yard) Yönetimi", true);
        setSize(750, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));
        arayuzuOlustur();
    }

    private void arayuzuOlustur() {

        JPanel ustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blokSecimi = new JComboBox<>();
        for (Blok b : veri.getBloklar()) {
            blokSecimi.addItem(b);
        }

        JTextField aramaAlani = new JTextField(14);
        JButton aramaBtn = new JButton("ID ile Ara");
        JButton temizleBtn = new JButton("Temizle");

        ustPanel.add(new JLabel("Blok:"));
        ustPanel.add(blokSecimi);
        ustPanel.add(new JLabel("  Konteyner ID:"));
        ustPanel.add(aramaAlani);
        ustPanel.add(aramaBtn);
        ustPanel.add(temizleBtn);
        add(ustPanel, BorderLayout.NORTH);

        Blok ilkBlok = veri.getBloklar().isEmpty() ? null : veri.getBloklar().get(0);
        tabloModeli = new KonteynerTableModel(
                ilkBlok != null ? ilkBlok.getIcindekiKonteynerler() : List.of());
        tablo = new JTable(tabloModeli);
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        JLabel ozetEtiketi = new JLabel();
        ozetEtiketiGuncelle(ozetEtiketi, ilkBlok);
        add(ozetEtiketi, BorderLayout.SOUTH);

        blokSecimi.addActionListener(e -> {
            Blok secilen = (Blok) blokSecimi.getSelectedItem();
            if (secilen != null) {
                tabloModeli.guncelle(secilen.getIcindekiKonteynerler());
                ozetEtiketiGuncelle(ozetEtiketi, secilen);
            }
        });

        aramaBtn.addActionListener(e -> {
            String aranan = aramaAlani.getText().trim().toUpperCase();
            if (aranan.isEmpty()) {
                return;
            }

            for (Blok b : veri.getBloklar()) {
                for (int i = 0; i < b.getIcindekiKonteynerler().size(); i++) {
                    Konteyner k = b.getIcindekiKonteynerler().get(i);
                    if (k.getKonteynerId().contains(aranan)) {
                        blokSecimi.setSelectedItem(b);
                        tabloModeli.guncelle(b.getIcindekiKonteynerler());
                        ozetEtiketiGuncelle(ozetEtiketi, b);
                        tablo.setRowSelectionInterval(i, i);
                        tablo.scrollRectToVisible(tablo.getCellRect(i, 0, true));
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(this,
                    "\"" + aranan + "\" ID'li konteyner hiçbir blokta bulunamadı.",
                    "Bulunamadı", JOptionPane.INFORMATION_MESSAGE);
        });

        temizleBtn.addActionListener(e -> {
            aramaAlani.setText("");
            tablo.clearSelection();
        });
    }

    private void ozetEtiketiGuncelle(JLabel etiket, Blok blok) {
        if (blok == null) {
            etiket.setText("  Blok bulunamadı.");
            return;
        }
        int kapasite = blok.getMaxSira() * blok.getMaxYigin() * blok.getMaxKat();
        int dolu = blok.getIcindekiKonteynerler().size();
        etiket.setText(String.format(
                "  %s  |  Tip: %s  |  Kapasite: %d  |  Dolu: %d  |  Boş: %d",
                blok.getBlokId(), blok.getTip(), kapasite, dolu, kapasite - dolu));
    }

    private static class KonteynerTableModel extends AbstractTableModel {

        private final String[] kolonlar = {"Konteyner ID", "Tip", "Yön", "Dolu/Boş", "Sıra", "Yığın", "Kat", "Tır"};
        private List<Konteyner> liste;

        KonteynerTableModel(List<Konteyner> liste) {
            this.liste = liste;
        }

        void guncelle(List<Konteyner> yeni) {
            this.liste = yeni;
            fireTableDataChanged();
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
            Konteyner k = liste.get(row);
            switch (col) {
                case 0:
                    return k.getKonteynerId();
                case 1:
                    return k.getTip();
                case 2:
                    return k.getYon();
                case 3:
                    return k.isDolu() ? "Dolu" : "Boş";
                case 4:
                    return k.getSira();
                case 5:
                    return k.getYigin();
                case 6:
                    return k.getKat();
                case 7:
                    return k.getTirPlaka() != null ? k.getTirPlaka() : "-";
                default:
                    return "";
            }
        }
    }
}
