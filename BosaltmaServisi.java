package liman.servis;

import liman.model.Blok;
import liman.model.Konteyner;
import liman.model.Gemi;
import liman.veri.LimanVeri;
import java.util.List;
import java.util.Random;

public class BosaltmaServisi {

    private final Random random = new Random();
    private final LimanVeri veri = LimanVeri.getInstance();

    
    public void gemiyiBosalt(Gemi gemi, List<Blok> bloklar) {
        gemi.setDurum(Gemi.Durum.BOSALTILIYOR);
        veri.logEkle("Tahliye", gemi.getGemiAdi()
                + " tahliyesi başladı. Toplam: " + gemi.getKonteynerListesi().size());

        int basarili = 0, basarisiz = 0;
        for (Konteyner k : gemi.getKonteynerListesi()) {
            if (k.getDurum() != Konteyner.Durum.GEMIDE) continue;
            Blok blok = uygunBlokBul(k, bloklar);
            if (blok == null) { basarisiz++; continue; }
            konteynerYerlestir(k, blok);
            basarili++;
        }

        gemi.setDurum(Gemi.Durum.TAHLIYE_TAMAMLANDI);
        veri.logEkle("Tahliye", gemi.getGemiAdi()
                + " tahliyesi tamamlandı. Başarılı: " + basarili
                + " | Başarısız: " + basarisiz);
    }

    
    public boolean tekKonteynerBosalt(Konteyner k, List<Blok> bloklar) {
        if (k.getDurum() != Konteyner.Durum.GEMIDE) return false;
        Blok blok = uygunBlokBul(k, bloklar);
        if (blok == null) return false;
        konteynerYerlestir(k, blok);
        return true;
    }

    
    public Blok uygunBlokBul(Konteyner k, List<Blok> bloklar) {
        Blok.BlokTipi hedef;
        if (!k.isDolu()) {
            hedef = Blok.BlokTipi.BOS_KONTEYNER;
        } else if (k.getTip() == Konteyner.Tip.REEFER) {
            hedef = Blok.BlokTipi.REEFER;
        } else {
            switch (k.getYon()) {
                case IHRACAT: hedef = Blok.BlokTipi.IHRACAT; break;
                case TRANSIT:
                case ITHALAT:
                default:      hedef = Blok.BlokTipi.ITHALAT; break;
            }
        }
        for (Blok b : bloklar) {
            if (b.getTip() == hedef && !b.doluMu()) return b;
        }
        return null;
    }

    
    private void konteynerYerlestir(Konteyner k, Blok blok) {
        k.setBlokId(blok.getBlokId());
        k.setSira(random.nextInt(blok.getMaxSira()) + 1);
        k.setYigin(random.nextInt(blok.getMaxYigin()) + 1);
        k.setKat(random.nextInt(blok.getMaxKat()) + 1);
        k.setDurum(Konteyner.Durum.SAHADA);
        blok.getIcindekiKonteynerler().add(k);
    }
}