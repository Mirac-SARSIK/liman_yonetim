package liman.servis;

import liman.model.*;
import liman.veri.LimanVeri;

import java.util.*;
import java.util.stream.Collectors;

public class OperasyonYoneticisi {

    private static OperasyonYoneticisi instance;
    private final LimanVeri veri = LimanVeri.getInstance();
    private final BosaltmaServisi bosaltma = new BosaltmaServisi();

    private Gemi tahliyeGemi = null;
    private Queue<Konteyner> tahliyeKuyrugu = null;
    private int tahliyeToplamKonteyner = 0;
    private int tahliyeIslenenSayisi = 0;
    private long sonIslemSaniyesi = 0;
    private boolean tahliyeAktif = false;
    private boolean duraksatildi = false;
    private static final int TAHLIYE_ARALIGI = 5; // saniye

    private boolean otomatikOnay = false;
    private static final int GATE_SURE = 4; // saniye

    private final List<Runnable> genelCB = new ArrayList<>();
    private final List<Runnable> tahliyCB = new ArrayList<>();
    private final List<Runnable> kapiCB = new ArrayList<>();

    private OperasyonYoneticisi() {
        LimanSaati.getInstance().dinleyiciEkle(this::tikle);
    }

    public static OperasyonYoneticisi getInstance() {
        if (instance == null) {
            instance = new OperasyonYoneticisi();
        }
        return instance;
    }

    private void tikle() {
        islemTahliye();
        islemOtomatikOnay();
        islemGateSayaclari();
        bilgilendir(genelCB);
        bilgilendir(tahliyCB);
        bilgilendir(kapiCB);
    }

    private void bilgilendir(List<Runnable> liste) {
        for (Runnable r : new ArrayList<>(liste)) {
            try {
                r.run();
            } catch (Exception ignored) {
            }
        }
    }

    public boolean tahliyeBaslat(Gemi gemi) {
        if (tahliyeAktif) {
            return false;
        }

        tahliyeGemi = gemi;
        tahliyeKuyrugu = new LinkedList<>();

        for (Konteyner k : gemi.getKonteynerListesi()) {
            if (k.getDurum() == Konteyner.Durum.GEMIDE) {
                tahliyeKuyrugu.add(k);
            }
        }

        tahliyeToplamKonteyner = tahliyeKuyrugu.size();
        tahliyeIslenenSayisi = 0;
        sonIslemSaniyesi = LimanSaati.getInstance().getGecenSaniye();
        tahliyeAktif = true;
        duraksatildi = false;

        gemi.setDurum(Gemi.Durum.BOSALTILIYOR);
        veri.logEkle("Tahliye", gemi.getGemiAdi()
                + " tahliyesi başladı. Toplam: " + tahliyeToplamKonteyner + " konteyner.");
        return true;
    }

    private void islemTahliye() {
        if (!tahliyeAktif || duraksatildi) {
            return;
        }
        if (tahliyeKuyrugu == null || tahliyeKuyrugu.isEmpty()) {
            return;
        }

        long simdi = LimanSaati.getInstance().getGecenSaniye();
        if (simdi - sonIslemSaniyesi < TAHLIYE_ARALIGI) {
            return;
        }

        // Rıhtımdaki vinç sayısı kadar konteyner işle
        int vincSayisi = aktifVincSayisi();
        int islenecek = Math.min(vincSayisi, tahliyeKuyrugu.size());
        int buTurIslenen = 0;

        for (int i = 0; i < islenecek; i++) {
            Konteyner k = tahliyeKuyrugu.peek();
            if (k == null) {
                break;
            }

            Blok uygun = bosaltma.uygunBlokBul(k, veri.getBloklar());
            if (uygun == null) {
                duraksatildi = true;
                veri.logEkle("Tahliye",
                        "⚠ Saha dolu! " + k.getKonteynerId()
                        + " için yer yok. Tırlar boşaltana kadar bekleniyor...");
                break;
            }

            tahliyeKuyrugu.poll();
            boolean ok = bosaltma.tekKonteynerBosalt(k, veri.getBloklar());
            if (ok) {
                tahliyeIslenenSayisi++;
                buTurIslenen++;
                veri.logEkle("Tahliye", k.getKonteynerId()
                        + " → " + k.getBlokId()
                        + " [" + k.getSira() + "," + k.getYigin() + "," + k.getKat() + "]");
            }
        }

        if (buTurIslenen > 0) {
            List<Konteyner> sahaDakilar = veri.getBloklar().stream()
                    .flatMap(b -> b.getIcindekiKonteynerler().stream())
                    .filter(k -> k.getDurum() == Konteyner.Durum.SAHADA)
                    .collect(Collectors.toList());
            veri.getKonteynerDAO().tumunuKaydet(sahaDakilar);
        }

        sonIslemSaniyesi = simdi;

        if (tahliyeKuyrugu.isEmpty()) {
            tahliyeAktif = false;
            tahliyeGemi.setDurum(Gemi.Durum.TAHLIYE_TAMAMLANDI);
            veri.getGemiDAO().kaydet(tahliyeGemi);
            veri.logEkle("Tahliye", tahliyeGemi.getGemiAdi() + " tahliyesi tamamlandı!");
        }
    }

    public int yukBindirme(Gemi gemi) {
        int yuklenen = 0;
        for (Blok b : veri.getBloklar()) {
            if (b.getTip() != Blok.BlokTipi.IHRACAT) {
                continue;
            }
            List<Konteyner> yuklenecekler = new ArrayList<>(b.getIcindekiKonteynerler());
            for (Konteyner k : yuklenecekler) {
                k.setDurum(Konteyner.Durum.GEMIDE);
                k.setBlokId(null);
                k.setGemiId(gemi.getGemiId());
                b.getIcindekiKonteynerler().remove(k);
                gemi.konteynerEkle(k);
                yuklenen++;
            }
        }
        if (yuklenen > 0) {
            gemi.setDurum(Gemi.Durum.YUKLENIYOR);
            veri.getGemiDAO().kaydet(gemi);
            veri.getKonteynerDAO().tumunuKaydet(gemi.getKonteynerListesi());
            veri.logEkle("Yük", gemi.getGemiAdi() + " gemisine " + yuklenen + " konteyner yüklendi.");
        }
        return yuklenen;
    }

    public void gemiyiRihtimindanAyir() {
        if (tahliyeGemi == null) {
            return;
        }
        for (Rihtim r : veri.getRihtimlar()) {
            if (tahliyeGemi.getGemiId().equals(r.getBaglıGemiId())) {
                r.setDurum(Rihtim.Durum.BOS);
                r.setBaglıGemiId(null);
                veri.getRihtimDAO().kaydet(r);
                break;
            }
        }
        tahliyeGemi.setDurum(Gemi.Durum.AYRILDI);
        veri.getGemiDAO().kaydet(tahliyeGemi);
        veri.logEkle("Operatör", tahliyeGemi.getGemiAdi() + " rıhtımdan ayrıldı.");
        tahliyeGemi = null;
        tahliyeAktif = false;
        duraksatildi = false;
    }

    public void tirOnayla(Tir tir) {
        if (tir.getDurum() != Tir.Durum.KAPIDA_BEKLIYOR) {
            return;
        }
        Konteyner k = konteynerBul(tir.getTasidigiKonteynerId());
        if (k == null) {
            return;
        }

        tir.setDurum(Tir.Durum.GOREVLI);
        tir.setKalanIslemSaniyesi(GATE_SURE);
        k.setDurum(Konteyner.Durum.TASINIYOR);

        veri.getTirDAO().kaydet(tir);
        veri.getKonteynerDAO().kaydet(k);
        veri.logEkle("Gate", tir.getPlaka() + " içeri alındı → " + k.getKonteynerId());
    }

    private void islemOtomatikOnay() {
        if (!otomatikOnay) {
            return;
        }
        for (Tir t : new ArrayList<>(veri.getTirlar())) {
            if (t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR) {
                tirOnayla(t);
            }
        }
    }

    private void islemGateSayaclari() {
        List<Tir> tamamlananlar = new ArrayList<>();
        for (Tir t : new ArrayList<>(veri.getTirlar())) {
            if (t.getDurum() == Tir.Durum.GOREVLI) {
                t.setKalanIslemSaniyesi(t.getKalanIslemSaniyesi() - 1);
                if (t.getKalanIslemSaniyesi() <= 0) {
                    tamamlananlar.add(t);
                }
            }
        }
        for (Tir t : tamamlananlar) {
            gateCikis(t);
        }
    }

    private void gateCikis(Tir tir) {
        Konteyner k = konteynerBul(tir.getTasidigiKonteynerId());
        if (k != null) {
            k.setDurum(Konteyner.Durum.LIMANDAN_AYRILDI);
            for (Blok b : veri.getBloklar()) {
                b.getIcindekiKonteynerler().remove(k);
            }
            veri.getKonteynerDAO().kaydet(k);
            veri.logEkle("Gate", tir.getPlaka() + " çıkış yaptı → " + k.getKonteynerId() + " limandan ayrıldı.");
        }
        veri.getTirDAO().sil(tir.getPlaka());
        veri.getTirlar().remove(tir);

        if (duraksatildi && tahliyeAktif
                && tahliyeKuyrugu != null && !tahliyeKuyrugu.isEmpty()) {
            Konteyner sonraki = tahliyeKuyrugu.peek();
            if (bosaltma.uygunBlokBul(sonraki, veri.getBloklar()) != null) {
                duraksatildi = false;
                sonIslemSaniyesi = LimanSaati.getInstance().getGecenSaniye();
                veri.logEkle("Tahliye", "Saha açıldı, tahliye devam ediyor...");
            }
        }
    }

    private Konteyner konteynerBul(String id) {
        if (id == null) {
            return null;
        }
        for (Blok b : veri.getBloklar()) {
            for (Konteyner k : b.getIcindekiKonteynerler()) {
                if (k.getKonteynerId().equals(id)) {
                    return k;
                }
            }
        }
        return null;
    }

    private int aktifVincSayisi() {
        if (tahliyeGemi == null) {
            return 1;
        }
        for (Rihtim r : veri.getRihtimlar()) {
            if (tahliyeGemi.getGemiId().equals(r.getBaglıGemiId())) {
                return Math.max(1, r.getVincSayisi());
            }
        }
        return 1;
    }

    public boolean isTahliyeAktif() {
        return tahliyeAktif;
    }

    public boolean isTahliiyeDuraksatildi() {
        return duraksatildi;
    }

    public Gemi getTahliyeGemi() {
        return tahliyeGemi;
    }

    public int getTahliyeToplamKonteyner() {
        return tahliyeToplamKonteyner;
    }

    public int getTahliyeIslenenSayisi() {
        return tahliyeIslenenSayisi;
    }

    public boolean isOtomatikOnay() {
        return otomatikOnay;
    }

    public void setOtomatikOnay(boolean b) {
        otomatikOnay = b;
    }

    public void genelGuncelleyiciEkle(Runnable r) {
        genelCB.add(r);
    }

    public void genelGuncelleyiciSil(Runnable r) {
        genelCB.remove(r);
    }

    public void tahliyeGuncelleyiciEkle(Runnable r) {
        tahliyCB.add(r);
    }

    public void tahliyeGuncelleyiciSil(Runnable r) {
        tahliyCB.remove(r);
    }

    public void kapiGuncelleyiciEkle(Runnable r) {
        kapiCB.add(r);
    }

    public void kapiGuncelleyiciSil(Runnable r) {
        kapiCB.remove(r);
    }
}
