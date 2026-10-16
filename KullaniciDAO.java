package liman.veri;

import java.sql.*;

public class KullaniciDAO {

    public boolean dogrula(String kullanici, String sifre) {
        String sql = "SELECT COUNT(*) FROM kullanici WHERE kullanici_adi=? AND sifre=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kullanici);
            ps.setString(2, sifre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.err.println("KullaniciDAO hata: " + e.getMessage()); }
        return false;
    }

    public void guncelle(String eskiKullanici, String yeniKullanici, String yeniSifre) {
        String sql = "UPDATE kullanici SET kullanici_adi=?, sifre=? WHERE kullanici_adi=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, yeniKullanici);
            ps.setString(2, yeniSifre);
            ps.setString(3, eskiKullanici);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("KullaniciDAO güncelle hata: " + e.getMessage()); }
    }

    public String getSifre(String kullaniciAdi) {
        String sql = "SELECT sifre FROM kullanici WHERE kullanici_adi=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kullaniciAdi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("sifre");
        } catch (SQLException e) { System.err.println("KullaniciDAO hata: " + e.getMessage()); }
        return null;
    }
}