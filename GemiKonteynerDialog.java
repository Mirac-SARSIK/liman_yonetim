package liman.ui.dialog;

import liman.model.Gemi;
import liman.model.Konteyner;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class GemiKonteynerDialog extends JDialog {

    public GemiKonteynerDialog(Frame parent, Gemi gemi) {
        super(parent, "Konteyner Listesi — " + gemi.getGemiAdi(), false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        JPanel bilgiPanel = new JPanel(new GridLayout(2, 4, 8, 4));
        bilgiPanel.setBorder(BorderFactory.createTitledBorder("Gemi Bilgileri"));

        long gemideKalan = gemi.getKonteynerListesi().stream()
                .filter(k -> k.getDurum() == Konteyner.Durum.GEMIDE).count();
        long ithalat = gemi.getKonteynerListesi().stream()
                .filter(k -> k.getYon() == Konteyner.Yon.ITHALAT).count();
        long ihracat = gemi.getKonteynerListesi().stream()
                .filter(k -> k.getYon() == Konteyner.Yon.IHRACAT).count();
        long transit = gemi.getKonteynerListesi().stream()
                .filter(k -> k.getYon() == Konteyner.Yon.TRANSIT).count();

        bilgiPanel.add(new JLabel("  ID: " + gemi.getGemiId()));
        bilgiPanel.add(new JLabel("Ad: " + gemi.getGemiAdi()));
        bilgiPanel.add(new JLabel("Kapasite: " + gemi.getKapasiteTEU() + " TEU"));
        bilgiPanel.add(new JLabel("Durum: " + gemi.getDurum()));
        bilgiPanel.add(new JLabel("  Gemide Kalan: " + gemideKalan + " / " + gemi.getKonteynerListesi().size()));
        bilgiPanel.add(new JLabel("İthalat: " + ithalat));
        bilgiPanel.add(new JLabel("İhracat: " + ihracat));
        bilgiPanel.add(new JLabel("Transit: " + transit));

        add(bilgiPanel, BorderLayout.NORTH);

        JTable tablo = new JTable(new GemiKonteynerModel(gemi.getKonteynerListesi()));
        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }

    private static class GemiKonteynerModel extends AbstractTableModel {

        private final String[] kolonlar = {"Konteyner ID", "Tip", "Yön", "Dolu/Boş", "Durum"};
        private final List<Konteyner> liste;

        GemiKonteynerModel(List<Konteyner> liste) {
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
                    return k.getDurum();
                default:
                    return "";
            }
        }
    }
}
