package liman.model;

public class Konteyner {

    public enum Tip {
        STANDART_20, STANDART_40, REEFER, ACIK_UST, BOS
    }

    public enum Yon {
        ITHALAT, IHRACAT, TRANSIT
    }

    public enum Durum {
        GEMIDE, BOSALTILIYOR, SAHADA, TASINIYOR, LIMANDAN_AYRILDI
    }

    private String konteynerId;
    private Tip tip;
    private Yon yon;
    private Durum durum;
    private boolean dolu;

    private String gemiId;
    private String blokId;
    private int sira;
    private int yigin;
    private int kat;
    private String tirPlaka;

    public Konteyner(String konteynerId, Tip tip, Yon yon, boolean dolu) {
        this.konteynerId = konteynerId;
        this.tip = tip;
        this.yon = yon;
        this.dolu = dolu;
        this.durum = Durum.GEMIDE;
    }

    public String getKonteynerId() {
        return konteynerId;
    }

    public Tip getTip() {
        return tip;
    }

    public Yon getYon() {
        return yon;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    public boolean isDolu() {
        return dolu;
    }

    public String getGemiId() {
        return gemiId;
    }

    public void setGemiId(String gemiId) {
        this.gemiId = gemiId;
    }

    public String getBlokId() {
        return blokId;
    }

    public void setBlokId(String blokId) {
        this.blokId = blokId;
    }

    public int getSira() {
        return sira;
    }

    public void setSira(int sira) {
        this.sira = sira;
    }

    public int getYigin() {
        return yigin;
    }

    public void setYigin(int yigin) {
        this.yigin = yigin;
    }

    public int getKat() {
        return kat;
    }

    public void setKat(int kat) {
        this.kat = kat;
    }

    public String getTirPlaka() {
        return tirPlaka;
    }

    public void setTirPlaka(String tirPlaka) {
        this.tirPlaka = tirPlaka;
    }

    @Override
    public String toString() {
        return konteynerId + " [" + tip + ", " + yon + ", " + durum + "]";
    }
}
