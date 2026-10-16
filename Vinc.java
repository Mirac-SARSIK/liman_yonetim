package liman.model;

public class Vinc {

    public enum Tip {
        STS,
        RTG
    }

    public enum Durum {
        BOS, MESGUL, ARIZALI
    }

    private String vincId;
    private Tip tip;
    private String baglıBirimId;
    private Durum durum;

    public Vinc(String vincId, Tip tip, String baglıBirimId) {
        this.vincId = vincId;
        this.tip = tip;
        this.baglıBirimId = baglıBirimId;
        this.durum = Durum.BOS;
    }

    public String getVincId() {
        return vincId;
    }

    public Tip getTip() {
        return tip;
    }

    public String getBaglıBirimId() {
        return baglıBirimId;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    @Override
    public String toString() {
        return vincId + " [" + tip + "] - " + durum;
    }
}
