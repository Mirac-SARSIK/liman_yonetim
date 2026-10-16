package liman.veri;

import liman.model.Konteyner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KonteynerDAO {

    public void kaydet(Konteyner k) {
        String sql = "INSERT INTO konteyner VALUES (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                + "durum=?, blok_id=?, sira=?, yigin=?, kat=?, tir_plaka=?, gemi_id=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k.getKonteynerId());
            ps.setString(2, k.getTip().name());
            ps.setString(3, k.getYon().name());
            ps.setBoolean(4, k.isDolu());
            ps.setString(5, k.getDurum().name());
            ps.setString(6, k.getGemiId());
            ps.setString(7, k.getBlokId());
            ps.setInt(8, k.getSira());
            ps.setInt(9, k.getYigin());
            ps.setInt(10, k.getKat());
            ps.setString(11, k.getTirPlaka());
            // ON DUPLICATE KEY kısmı
            ps.setString(12, k.getDurum().name());
            ps.setString(13, k.getBlokId());
            ps.setInt(14, k.getSira());
            ps.setInt(15, k.getYigin());
            ps.setInt(16, k.getKat());
            ps.setString(17, k.getTirPlaka());
            ps.setString(18, k.getGemiId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("KonteynerDAO kaydet hata: " + e.getMessage());
        }
    }

    public List<Konteyner> gemiKonteynerleriniGetir(String gemiId) {
        List<Konteyner> liste = new ArrayList<>();
        String sql = "SELECT * FROM konteyner WHERE gemi_id=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, gemiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(satirOku(rs));
            }
        } catch (SQLException e) {
            System.err.println("KonteynerDAO hata: " + e.getMessage());
        }
        return liste;
    }

    public List<Konteyner> blokKonteynerleriniGetir(String blokId) {
        List<Konteyner> liste = new ArrayList<>();
        String sql = "SELECT * FROM konteyner WHERE blok_id=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, blokId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(satirOku(rs));
            }
        } catch (SQLException e) {
            System.err.println("KonteynerDAO hata: " + e.getMessage());
        }
        return liste;
    }

    public List<Konteyner> gemidekileriGetir(String gemiId) {
        List<Konteyner> liste = new ArrayList<>();
        String sql = "SELECT * FROM konteyner WHERE gemi_id=? AND durum='GEMIDE'";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, gemiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(satirOku(rs));
            }
        } catch (SQLException e) {
            System.err.println("KonteynerDAO hata: " + e.getMessage());
        }
        return liste;
    }

    public List<Konteyner> bloktakileriGetir(String blokId) {
        List<Konteyner> liste = new ArrayList<>();
        String sql = "SELECT * FROM konteyner WHERE blok_id=? AND durum='SAHADA'";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, blokId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(satirOku(rs));
            }
        } catch (SQLException e) {
            System.err.println("KonteynerDAO hata: " + e.getMessage());
        }
        return liste;
    }

    private Konteyner satirOku(ResultSet rs) throws SQLException {
        Konteyner k = new Konteyner(
                rs.getString("konteyner_id"),
                Konteyner.Tip.valueOf(rs.getString("tip")),
                Konteyner.Yon.valueOf(rs.getString("yon")),
                rs.getBoolean("dolu")
        );
        k.setDurum(Konteyner.Durum.valueOf(rs.getString("durum")));
        k.setGemiId(rs.getString("gemi_id"));
        k.setBlokId(rs.getString("blok_id"));
        k.setSira(rs.getInt("sira"));
        k.setYigin(rs.getInt("yigin"));
        k.setKat(rs.getInt("kat"));
        k.setTirPlaka(rs.getString("tir_plaka"));
        return k;
    }

    public void tumunuKaydet(List<Konteyner> liste) {
        for (Konteyner k : liste) {
            kaydet(k);
        }
    }
}
