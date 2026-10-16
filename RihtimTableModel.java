package liman.ui.tablo;

import liman.model.Gemi;
import liman.model.Konteyner;
import liman.model.Rihtim;
import liman.veri.LimanVeri;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class RihtimTableModel extends AbstractTableModel {

    private final String[] kolonlar = {"Rıhtım", "Durum", "Gemi", "Vinç Sayısı", "Kalan Konteyner"};
    private List<Rihtim> rihtimlar;
    private LimanVeri veri = LimanVeri.getInstance();

    public RihtimTableModel(List<Rihtim> rihtimlar) {
        this.rihtimlar = rihtimlar;
    }

    @Override
    public int getRowCount() { return rihtimlar.size(); }

    @Override
    public int getColumnCount() { return kolonlar.length; }

    @Override
    public String getColumnName(int col) { return kolonlar[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Rihtim r = rihtimlar.get(row);
        switch (col) {
            case 0: return r.getRihtimId();
            case 1: return r.getDurum();
            case 2: return r.getBaglıGemiId() != null ? r.getBaglıGemiId() : "-";
            case 3: return r.getVincSayisi();
            case 4: return kalanKonteynerSayisi(r);
            default: return "";
        }
    }

    private String kalanKonteynerSayisi(Rihtim r) {
        if (r.getBaglıGemiId() == null) return "-";

        for (Gemi g : veri.getGemiler()) {
            if (g.getGemiId().equals(r.getBaglıGemiId())) {
                long kalan = g.getKonteynerListesi().stream()
                        .filter(k -> k.getDurum() == Konteyner.Durum.GEMIDE)
                        .count();
                return kalan + " / " + g.getKonteynerListesi().size();
            }
        }
        return "-";
    }

    public void yenile() {
        fireTableDataChanged();
    }
}