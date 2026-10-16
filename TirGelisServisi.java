package liman.servis;

import liman.model.Blok;
import liman.model.Konteyner;
import liman.model.Tir;
import liman.veri.LimanVeri;

import java.util.Random;

public class TirGelisServisi {

    private LimanVeri veri = LimanVeri.getInstance();
    private LimanSaati saat = LimanSaati.getInstance();
    private Random random = new Random();

    private long sonGelisSaniyesi = 0;
    private int sonrakiGelisSuresi = rastgeleAralik();
    private int tirSayaci = 1;

    private Runnable saatDinleyici;

    public void baslat() {
        saatDinleyici = this::saniyeTikladi;
        saat.dinleyiciEkle(saatDinleyici);
    }

    public void durdur() {
        saat.dinleyiciSil(saatDinleyici);
    }

    private int rastgeleAralik() {
        return 4 + random.nextInt(6);
    }

    private void saniyeTikladi() {
        long simdi = saat.getGecenSaniye();
        if (simdi - sonGelisSaniyesi < sonrakiGelisSuresi) {
            return;
        }

        Random random = new Random();

        char rastgeleKucukHarf = (char) (random.nextInt(26) + 'A');
        System.out.println();

        sonGelisSaniyesi = simdi;
        sonrakiGelisSuresi = rastgeleAralik();

        Konteyner hedefKonteyner = tasinmamisRastgeleKonteynerBul();
        if (hedefKonteyner == null) {
            return;
        }

        Tir yeniTir = new Tir("34 GT " + (100 + tirSayaci), rastgeleKucukHarf + "******" + rastgeleKucukHarf + "******");
        tirSayaci++;
        yeniTir.setDurum(Tir.Durum.KAPIDA_BEKLIYOR);
        yeniTir.setTasidigiKonteynerId(hedefKonteyner.getKonteynerId());

        veri.getTirlar().add(yeniTir);
        veri.logEkle("Gate", yeniTir.getPlaka() + " kapıya geldi, " + hedefKonteyner.getKonteynerId() + " talep ediyor. Onay bekliyor.");
    }

    private Konteyner tasinmamisRastgeleKonteynerBul() {
        java.util.List<Konteyner> adaylar = new java.util.ArrayList<>();

        for (Blok b : veri.getBloklar()) {
            for (Konteyner k : b.getIcindekiKonteynerler()) {
                if (k.getDurum() == Konteyner.Durum.SAHADA && !zatenTalepEdilmisMi(k)) {
                    adaylar.add(k);
                }
            }
        }

        if (adaylar.isEmpty()) {
            return null;
        }
        return adaylar.get(random.nextInt(adaylar.size()));
    }

    private boolean zatenTalepEdilmisMi(Konteyner k) {
        for (Tir t : veri.getTirlar()) {
            if ((t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR || t.getDurum() == Tir.Durum.GOREVLI)
                    && k.getKonteynerId().equals(t.getTasidigiKonteynerId())) {
                return true;
            }
        }
        return false;
    }
}
