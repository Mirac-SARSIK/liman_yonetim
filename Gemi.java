package liman.model;

import java.util.ArrayList;
import java.util.List;

public class Gemi {

    public enum Durum {
        BEKLEMEDE, YANASMAYA_GELIYOR, YANASTI, BOSALTILIYOR, TAHLIYE_TAMAMLANDI, YUKLENIYOR, AYRILDI
    }

    private String gemiId;
    private String gemiAdi;
    private double uzunluk;
    private double genislik;
    private double suCekimi;
    private int kapasiteTEU;
    private Durum durum;
    private String baglıRihtimId;

    private List<Konteyner> konteynerListesi;

    public Gemi(String gemiId, String gemiAdi, double uzunluk, double genislik,
            double suCekimi, int kapasiteTEU) {
        this.gemiId = gemiId;
        this.gemiAdi = gemiAdi;
        this.uzunluk = uzunluk;
        this.genislik = genislik;
        this.suCekimi = suCekimi;
        this.kapasiteTEU = kapasiteTEU;
        this.durum = Durum.BEKLEMEDE;
        this.konteynerListesi = new ArrayList<>();
    }

    public void konteynerEkle(Konteyner k) {
        k.setGemiId(this.gemiId);
        konteynerListesi.add(k);
    }

    public String getGemiId() {
        return gemiId;
    }

    public String getGemiAdi() {
        return gemiAdi;
    }

    public double getUzunluk() {
        return uzunluk;
    }

    public double getGenislik() {
        return genislik;
    }

    public double getSuCekimi() {
        return suCekimi;
    }

    public int getKapasiteTEU() {
        return kapasiteTEU;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    public String getBaglıRihtimId() {
        return baglıRihtimId;
    }

    public void setBaglıRihtimId(String baglıRihtimId) {
        this.baglıRihtimId = baglıRihtimId;
    }

    public List<Konteyner> getKonteynerListesi() {
        return konteynerListesi;
    }

    @Override
    public String toString() {
        return gemiAdi + " (" + gemiId + ") - " + durum;
    }
}
