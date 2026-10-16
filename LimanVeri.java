package liman.veri;

import liman.model.*;
import java.util.ArrayList;
import java.util.List;

public class LimanVeri {

    private static LimanVeri instance;

    private List<Gemi> gemiler = new ArrayList<>();
    private List<Rihtim> rihtimlar = new ArrayList<>();
    private List<Blok> bloklar = new ArrayList<>();
    private List<Tir> tirlar = new ArrayList<>();
    private List<String> olayGunlugu = new ArrayList<>();

    private String aktifKullaniciAdi = "";

    // DAO'lar
    private final RihtimDAO rihtimDAO = new RihtimDAO();
    private final BlokDAO blokDAO = new BlokDAO();
    private final GemiDAO gemiDAO = new GemiDAO();
    private final KonteynerDAO konteynerDAO = new KonteynerDAO();
    private final TirDAO tirDAO = new TirDAO();
    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();

    private LimanVeri() {
        veritabanindenYukle();
    }

    public static LimanVeri getInstance() {
        if (instance == null) {
            instance = new LimanVeri();
        }
        return instance;
    }

    private void veritabanindenYukle() {
        rihtimlar = rihtimDAO.hepsiniGetir();
        bloklar = blokDAO.hepsiniGetir();

        if (rihtimlar.isEmpty()) {
            varsayilanRihtimlariYukle();
        }
        if (bloklar.isEmpty()) {
            varsayilanBloklariYukle();
        }

        gemiler = gemiDAO.hepsiniGetir();
        for (Gemi g : gemiler) {
            List<Konteyner> gemidekilar = konteynerDAO.gemidekileriGetir(g.getGemiId());
            for (Konteyner k : gemidekilar) {
                k.setGemiId(g.getGemiId());
                g.getKonteynerListesi().add(k);
            }
        }

        for (Blok b : bloklar) {
            List<Konteyner> bloktakiler = konteynerDAO.bloktakileriGetir(b.getBlokId());
            b.getIcindekiKonteynerler().addAll(bloktakiler);
        }

        List<Tir> tumTirlar = tirDAO.hepsiniGetir();
        for (Tir t : tumTirlar) {
            if (t.getDurum() != Tir.Durum.BOS && t.getDurum() != Tir.Durum.TAMAMLANDI) {
                tirlar.add(t);
            }
        }

        logEkle("Sistem", "Liman Yönetim Sistemi başlatıldı (Veritabanı bağlantısı aktif).");
        logEkle("Sistem", "Rıhtım ve saha sensörleri çevrimiçi.");
    }

    private void varsayilanRihtimlariYukle() {
        rihtimlar.add(new Rihtim("Rıhtım 1", 300, 15, 3));
        rihtimlar.add(new Rihtim("Rıhtım 2", 250, 12, 2));
        rihtimlar.add(new Rihtim("Rıhtım 3", 280, 14, 3));
        rihtimDAO.tumunuKaydet(rihtimlar);
    }

    private void varsayilanBloklariYukle() {
        bloklar.add(new Blok("Blok A (İthalat)", Blok.BlokTipi.ITHALAT, 5, 5, 4));
        bloklar.add(new Blok("Blok B (İhracat)", Blok.BlokTipi.IHRACAT, 6, 5, 5));
        bloklar.add(new Blok("Blok C (Reefer)", Blok.BlokTipi.REEFER, 3, 3, 2));
        bloklar.add(new Blok("Blok D (Boş Kont.)", Blok.BlokTipi.BOS_KONTEYNER, 4, 4, 3));
        blokDAO.tumunuKaydet(bloklar);
    }

    public void hepsiniKaydet() {
        rihtimDAO.tumunuKaydet(rihtimlar);
        blokDAO.tumunuKaydet(bloklar);
        gemiDAO.tumunuKaydet(gemiler);

        for (Gemi g : gemiler) {
            konteynerDAO.tumunuKaydet(g.getKonteynerListesi());
        }

        for (Blok b : bloklar) {
            konteynerDAO.tumunuKaydet(b.getIcindekiKonteynerler());
        }

        tirDAO.tumunuKaydet(tirlar);
    }

    public void logEkle(String kaynak, String mesaj) {
        String satir = "[" + kaynak + "] " + mesaj;
        olayGunlugu.add(satir);
        // Log'u da veritabanına yaz (arka planda)
        new Thread(() -> {
            try (java.sql.Connection c = VeriTabaniBaglantisi.getBaglanti(); java.sql.PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO olay_gunlugu (kaynak, mesaj) VALUES (?,?)")) {
                ps.setString(1, kaynak);
                ps.setString(2, mesaj);
                ps.executeUpdate();
            } catch (Exception e) {
                /* log hataları sessizce geç */ }
        }).start();
    }

    // =========================================================
    // Kullanıcı işlemleri (artık DB'den)
    // =========================================================
    public boolean girisDogrula(String kullanici, String sifre) {
        boolean sonuc = kullaniciDAO.dogrula(kullanici, sifre);
        if (sonuc) {
            aktifKullaniciAdi = kullanici;
        }
        return sonuc;
    }

    public String getSifre() {
        return kullaniciDAO.getSifre(aktifKullaniciAdi);
    }

    public String getKullaniciAdi() {
        return aktifKullaniciAdi;
    }

    public void kullaniciBilgileriniGuncelle(String yeniKullanici, String yeniSifre) {
        kullaniciDAO.guncelle(aktifKullaniciAdi, yeniKullanici, yeniSifre);
        aktifKullaniciAdi = yeniKullanici;
        logEkle("Sistem", "Kullanıcı bilgileri güncellendi.");
    }

    // =========================================================
    // Getter'lar
    // =========================================================
    public List<Gemi> getGemiler() {
        return gemiler;
    }

    public List<Rihtim> getRihtimlar() {
        return rihtimlar;
    }

    public List<Blok> getBloklar() {
        return bloklar;
    }

    public List<Tir> getTirlar() {
        return tirlar;
    }

    public List<String> getOlayGunlugu() {
        return olayGunlugu;
    }

    public RihtimDAO getRihtimDAO() {
        return rihtimDAO;
    }

    public BlokDAO getBlokDAO() {
        return blokDAO;
    }

    public GemiDAO getGemiDAO() {
        return gemiDAO;
    }

    public KonteynerDAO getKonteynerDAO() {
        return konteynerDAO;
    }

    public TirDAO getTirDAO() {
        return tirDAO;
    }
}
