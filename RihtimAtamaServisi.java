package liman.servis;

import liman.model.Gemi;
import liman.model.Rihtim;
import java.util.List;

public class RihtimAtamaServisi {

    public boolean gemiyiRihtimaAta(Gemi gemi, List<Rihtim> rihtimlar) {

        for (Rihtim rihtim : rihtimlar) {
            if (rihtim.gemiyeUygunMu(gemi)) {

                rihtim.setDurum(Rihtim.Durum.MESGUL);
                rihtim.setBaglıGemiId(gemi.getGemiId());

                gemi.setBaglıRihtimId(rihtim.getRihtimId());
                gemi.setDurum(Gemi.Durum.YANASTI);

                System.out.println(gemi.getGemiAdi() + " -> " + rihtim.getRihtimId() + " rıhtımına yanaştı.");
                return true;
            }
        }

        System.out.println(gemi.getGemiAdi() + " için uygun rıhtım bulunamadı, gemi beklemede.");
        gemi.setDurum(Gemi.Durum.BEKLEMEDE);
        return false;
    }

    public void rihtimiBosalt(Rihtim rihtim) {
        rihtim.setDurum(Rihtim.Durum.BOS);
        rihtim.setBaglıGemiId(null);
    }
}
