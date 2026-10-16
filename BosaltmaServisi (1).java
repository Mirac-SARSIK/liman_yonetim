package liman.util;

import liman.model.Blok;
import liman.model.Konteyner;
import liman.model.Gemi;
import java.util.List;
import java.util.Random;

public class BosaltmaServisi {

    private Random random = new Random();

    public void gemiyiBosalt(Gemi gemi, List<Blok> bloklar) {

        gemi.setDurum(Gemi.Durum.BOSALTILIYOR);

        for (Konteyner konteyner : gemi.getKonteynerListesi()) {

            Blok uygunBlok = uygunBlokBul(konteyner, bloklar);

            if (uygunBlok == null) {
                System.out.println(konteyner.getKonteynerId() + " için uygun/boş blok bulunamadı!");
                continue;
            }

            konteynerBlokaYerlestir(konteyner, uygunBlok);
        }

        System.out.println(gemi.getGemiAdi() + " gemisinin boşaltması tamamlandı.");
    }

    public Blok uygunBlokBul(Konteyner konteyner, List<Blok> bloklar) {

        Blok.BlokTipi hedefTip;

        if (!konteyner.isDolu()) {
            hedefTip = Blok.BlokTipi.BOS_KONTEYNER;
        } else if (konteyner.getTip() == Konteyner.Tip.REEFER) {
            hedefTip = Blok.BlokTipi.REEFER;
        } else {
            switch (konteyner.getYon()) {
                case ITHALAT:
                    hedefTip = Blok.BlokTipi.ITHALAT;
                    break;
                case IHRACAT:
                    hedefTip = Blok.BlokTipi.IHRACAT;
                    break;
                case TRANSIT:
                    // Transit konteynerleri basitçe ithalat bloğuna koyuyoruz (gemiye yüklenmeyi bekleyecekler)
                    hedefTip = Blok.BlokTipi.ITHALAT;
                    break;
                default:
                    hedefTip = Blok.BlokTipi.ITHALAT;
            }
        }

        for (Blok blok : bloklar) {
            if (blok.getTip() == hedefTip && !blok.doluMu()) {
                return blok;
            }
        }

        return null;
    }

    private void konteynerBlokaYerlestir(Konteyner konteyner, Blok blok) {
        konteyner.setBlokId(blok.getBlokId());
        konteyner.setSira(random.nextInt(blok.getMaxSira()) + 1);
        konteyner.setYigin(random.nextInt(blok.getMaxYigin()) + 1);
        konteyner.setKat(random.nextInt(blok.getMaxKat()) + 1);

        konteyner.setDurum(Konteyner.Durum.SAHADA);
        blok.getIcindekiKonteynerler().add(konteyner);

        System.out.println(konteyner.getKonteynerId() + " -> " + blok.getBlokId()
                + " [" + konteyner.getSira() + "," + konteyner.getYigin() + "," + konteyner.getKat() + "]");
    }
}
