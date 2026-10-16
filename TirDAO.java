package liman.veri;

import liman.model.Tir;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TirDAO {

    public void kaydet(Tir t) {
        String sql = "INSERT INTO tir VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                   + "durum=?, tasidigi_konteyner_id=?, kalan_islem_saniyesi=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getPlaka());
            ps.setString(2, t.getSoforAdi());
            ps.setString(3, t.getDurum().name());
            ps.setString(4, t.getTasidigiKonteynerId());
            ps.setInt(5, t.getKalanIslemSaniyesi());
            ps.setString(6, t.getDurum().name());
            ps.setString(7, t.getTasidigiKonteynerId());
            ps.setInt(8, t.getKalanIslemSaniyesi());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("TirDAO kaydet hata: " + e.getMessage()); }
    }

    public List<Tir> hepsiniGetir() {
        List<Tir> liste = new ArrayList<>();
        String sql = "SELECT * FROM tir";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Tir t = new Tir(rs.getString("plaka"), rs.getString("sofor_adi"));
                t.setDurum(Tir.Durum.valueOf(rs.getString("durum")));
                t.setTasidigiKonteynerId(rs.getString("tasidigi_konteyner_id"));
                t.setKalanIslemSaniyesi(rs.getInt("kalan_islem_saniyesi"));
                liste.add(t);
            }
        } catch (SQLException e) { System.err.println("TirDAO hata: " + e.getMessage()); }
        return liste;
    }

    public void sil(String plaka) {
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement("DELETE FROM tir WHERE plaka=?")) {
            ps.setString(1, plaka);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("TirDAO sil hata: " + e.getMessage()); }
    }

    public void tumunuKaydet(List<Tir> liste) {
        for (Tir t : liste) kaydet(t);
    }
}