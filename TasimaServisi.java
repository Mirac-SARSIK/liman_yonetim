package liman.servis;

import liman.model.Konteyner;
import liman.model.Tir;

public class TasimaServisi {

    public boolean konteyneriTiraAta(Konteyner konteyner, Tir tir) {

        if (konteyner.getDurum() != Konteyner.Durum.SAHADA) {
            System.out.println("HATA: " + konteyner.getKonteynerId() + " sahada değil, atama yapılamaz. Durum: " + konteyner.getDurum());
            return false;
        }

        if (tir.getDurum() != Tir.Durum.BOS) {
            System.out.println("HATA: " + tir.getPlaka() + " plakalı tır şu an müsait değil. Durum: " + tir.getDurum());
            return false;
        }

        konteyner.setTirPlaka(tir.getPlaka());
        konteyner.setDurum(Konteyner.Durum.TASINIYOR);

        tir.setTasidigiKonteynerId(konteyner.getKonteynerId());
        tir.setDurum(Tir.Durum.GOREVLI);

        System.out.println(tir.getPlaka() + " plakalı tır, " + konteyner.getKonteynerId() + " konteynerine atandı.");
        return true;
    }

    public boolean gateCikisiOnayla(Konteyner konteyner, Tir tir) {

        if (!tir.getPlaka().equals(konteyner.getTirPlaka())) {
            System.out.println("HATA: Bu tır bu konteynere atanmamış!");
            return false;
        }

        konteyner.setDurum(Konteyner.Durum.LIMANDAN_AYRILDI);
        tir.setDurum(Tir.Durum.TAMAMLANDI);

        System.out.println(tir.getPlaka() + " gate'ten çıkış yaptı, " + konteyner.getKonteynerId() + " limandan ayrıldı.");
        return true;
    }
}
