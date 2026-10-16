package liman.model;

import java.util.ArrayList;
import java.util.List;

public class Blok {

    public enum BlokTipi {
        ITHALAT, IHRACAT, REEFER, BOS_KONTEYNER, TEHLIKELI
    }

    private String blokId;
    private BlokTipi tip;
    private int maxSira;
    private int maxYigin;
    private int maxKat;

    private List<Konteyner> icindekiKonteynerler;

    public Blok(String blokId, BlokTipi tip, int maxSira, int maxYigin, int maxKat) {
        this.blokId = blokId;
        this.tip = tip;
        this.maxSira = maxSira;
        this.maxYigin = maxYigin;
        this.maxKat = maxKat;
        this.icindekiKonteynerler = new ArrayList<>();
    }

    public boolean doluMu() {
        return icindekiKonteynerler.size() >= (maxSira * maxYigin * maxKat);
    }

    public String getBlokId() {
        return blokId;
    }

    public BlokTipi getTip() {
        return tip;
    }

    public int getMaxSira() {
        return maxSira;
    }

    public int getMaxYigin() {
        return maxYigin;
    }

    public int getMaxKat() {
        return maxKat;
    }

    public void setTip(BlokTipi tip) {
        this.tip = tip;
    }

    public void setMaxSira(int maxSira) {
        this.maxSira = maxSira;
    }

    public void setMaxYigin(int maxYigin) {
        this.maxYigin = maxYigin;
    }

    public void setMaxKat(int maxKat) {
        this.maxKat = maxKat;
    }

    public List<Konteyner> getIcindekiKonteynerler() {
        return icindekiKonteynerler;
    }

    @Override
    public String toString() {
        return blokId + " [" + tip + "] (" + icindekiKonteynerler.size() + " konteyner)";
    }
}
