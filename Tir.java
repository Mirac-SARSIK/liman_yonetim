package liman.model;

public class Tir {

    public enum Durum {
        BOS, KAPIDA_BEKLIYOR, GOREVLI, LIMANDA, TAMAMLANDI
    }

    private int kalanIslemSaniyesi;

    public int getKalanIslemSaniyesi() {
        return kalanIslemSaniyesi;
    }

    public void setKalanIslemSaniyesi(int kalanIslemSaniyesi) {
        this.kalanIslemSaniyesi = kalanIslemSaniyesi;
    }

    private String plaka;
    private String soforAdi;
    private Durum durum;
    private String tasidigiKonteynerId;

    public Tir(String plaka, String soforAdi) {
        this.plaka = plaka;
        this.soforAdi = soforAdi;
        this.durum = Durum.BOS;
    }

    public String getPlaka() {
        return plaka;
    }

    public String getSoforAdi() {
        return soforAdi;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    public String getTasidigiKonteynerId() {
        return tasidigiKonteynerId;
    }

    public void setTasidigiKonteynerId(String tasidigiKonteynerId) {
        this.tasidigiKonteynerId = tasidigiKonteynerId;
    }

    @Override
    public String toString() {
        return plaka + " (" + soforAdi + ") - " + durum;
    }
}
