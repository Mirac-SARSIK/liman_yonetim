package liman.ui.tablo;

import liman.model.Tir;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class TirTableModel extends AbstractTableModel {

    private final String[] kolonlar = {"Tır Plakası", "Şoför", "Hedef Konteyner", "Durum", "Kalan Süre"};
    private List<Tir> tumTirlar;

    public TirTableModel(List<Tir> tirlar) {
        this.tumTirlar = tirlar;
    }

    private List<Tir> getAktifler() {
        return tumTirlar.stream()
                .filter(t -> t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR
                          || t.getDurum() == Tir.Durum.GOREVLI)
                .collect(Collectors.toList());
    }

    @Override public int getRowCount() { return getAktifler().size(); }
    @Override public int getColumnCount() { return kolonlar.length; }
    @Override public String getColumnName(int col) { return kolonlar[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        List<Tir> aktif = getAktifler();
        if (row >= aktif.size()) return "";
        Tir t = aktif.get(row);
        switch (col) {
            case 0: return t.getPlaka();
            case 1: return t.getSoforAdi();
            case 2: return t.getTasidigiKonteynerId() != null ? t.getTasidigiKonteynerId() : "-";
            case 3: return t.getDurum();
            case 4: return t.getDurum() == Tir.Durum.GOREVLI ? t.getKalanIslemSaniyesi() + " sn" : "Onay bekleniyor";
            default: return "";
        }
    }

    public void yenile() { fireTableDataChanged(); }
}