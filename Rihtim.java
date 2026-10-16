package liman.model;

public class Rihtim {

    public enum Durum {
        BOS, MESGUL, BAKIMDA
    }

    private String rihtimId;
    private double uzunluk;
    private double derinlik;
    private int vincSayisi;
    private Durum durum;
    private String baglıGemiId;

    public Rihtim(String rihtimId, double uzunluk, double derinlik, int vincSayisi) {
        this.rihtimId = rihtimId;
        this.uzunluk = uzunluk;
        this.derinlik = derinlik;
        this.vincSayisi = vincSayisi;
        this.durum = Durum.BOS;
    }

    public boolean gemiyeUygunMu(Gemi gemi) {
        return gemi.getUzunluk() <= this.uzunluk
                && gemi.getSuCekimi() <= this.derinlik
                && this.durum == Durum.BOS;
    }

    public String getRihtimId() {
        return rihtimId;
    }

    public double getUzunluk() {
        return uzunluk;
    }

    public double getDerinlik() {
        return derinlik;
    }

    public int getVincSayisi() {
        return vincSayisi;
    }

    public void setUzunluk(double uzunluk) {
        this.uzunluk = uzunluk;
    }

    public void setDerinlik(double derinlik) {
        this.derinlik = derinlik;
    }

    public void setVincSayisi(int vincSayisi) {
        this.vincSayisi = vincSayisi;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    public String getBaglıGemiId() {
        return baglıGemiId;
    }

    public void setBaglıGemiId(String baglıGemiId) {
        this.baglıGemiId = baglıGemiId;
    }

    @Override
    public String toString() {
        return rihtimId + " [" + uzunluk + "m, " + derinlik + "m derinlik] - " + durum;
    }
}
