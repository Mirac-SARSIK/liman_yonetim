package liman.ui.tablo;

import liman.model.Blok;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class BlokTableModel extends AbstractTableModel {

    private final String[] kolonlar = {"Blok", "Kapasite", "Dolu", "Boş"};
    private List<Blok> bloklar;

    public BlokTableModel(List<Blok> bloklar) {
        this.bloklar = bloklar;
    }

    @Override
    public int getRowCount() { return bloklar.size(); }

    @Override
    public int getColumnCount() { return kolonlar.length; }

    @Override
    public String getColumnName(int col) { return kolonlar[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Blok b = bloklar.get(row);
        int kapasite = b.getMaxSira() * b.getMaxYigin() * b.getMaxKat();
        int dolu = b.getIcindekiKonteynerler().size();
        switch (col) {
            case 0: return b.getBlokId();
            case 1: return kapasite;
            case 2: return dolu;
            case 3: return kapasite - dolu;
            default: return "";
        }
    }

    public void yenile() {
        fireTableDataChanged();
    }
}