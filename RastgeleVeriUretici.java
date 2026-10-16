package liman.servis;

import liman.model.Gemi;
import liman.model.Konteyner;
import liman.util.IdUretici;
import java.util.Random;

public class RastgeleVeriUretici {

    private static Random random = new Random();

    public static Gemi rastgeleGemiUret() {
        String id = IdUretici.gemiIdUret();
        String[] isimler = {"Marmara Star", "Ege Glory", "Karadeniz Pearl", "Akdeniz Hope", "Bosphorus Trader"};
        String ad = isimler[random.nextInt(isimler.length)];

        double uzunluk = 150 + random.nextInt(200);
        double genislik = 20 + random.nextInt(30);
        double suCekimi = 8 + random.nextInt(10);
        int kapasite = 1000 + random.nextInt(9000);

        Gemi gemi = new Gemi(id, ad, uzunluk, genislik, suCekimi, kapasite);

        int konteynerSayisi = 20 + random.nextInt(80);
        for (int i = 0; i < konteynerSayisi; i++) {
            gemi.konteynerEkle(rastgeleKonteynerUret());
        }

        return gemi;
    }

    public static Konteyner rastgeleKonteynerUret() {
        String id = IdUretici.konteynerIdUret();

        Konteyner.Tip[] tipler = Konteyner.Tip.values();
        Konteyner.Tip tip = tipler[random.nextInt(tipler.length)];

        Konteyner.Yon yon;
        int sans = random.nextInt(100);
        if (sans < 60) {
            yon = Konteyner.Yon.ITHALAT;
        } else if (sans < 90) {
            yon = Konteyner.Yon.IHRACAT;
        } else {
            yon = Konteyner.Yon.TRANSIT;
        }

        boolean dolu = random.nextInt(100) < 85;

        return new Konteyner(id, tip, yon, dolu);
    }
}
