package liman.veri;

import liman.model.Gemi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GemiDAO {

    public void kaydet(Gemi g) {
        String sql = "INSERT INTO gemi VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                   + "gemi_adi=?, durum=?, bagli_rihtim_id=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getGemiId());
            ps.setString(2, g.getGemiAdi());
            ps.setDouble(3, g.getUzunluk());
            ps.setDouble(4, g.getGenislik());
            ps.setDouble(5, g.getSuCekimi());
            ps.setInt(6, g.getKapasiteTEU());
            ps.setString(7, g.getDurum().name());
            ps.setString(8, g.getBaglıRihtimId());
            ps.setString(9, g.getGemiAdi());
            ps.setString(10, g.getDurum().name());
            ps.setString(11, g.getBaglıRihtimId());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("GemiDAO kaydet hata: " + e.getMessage()); }
    }

    public List<Gemi> hepsiniGetir() {
        List<Gemi> liste = new ArrayList<>();
        String sql = "SELECT * FROM gemi";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Gemi g = new Gemi(
                    rs.getString("gemi_id"),
                    rs.getString("gemi_adi"),
                    rs.getDouble("uzunluk"),
                    rs.getDouble("genislik"),
                    rs.getDouble("su_cekimi"),
                    rs.getInt("kapasite_teu")
                );
                g.setDurum(Gemi.Durum.valueOf(rs.getString("durum")));
                g.setBaglıRihtimId(rs.getString("bagli_rihtim_id"));
                liste.add(g);
            }
        } catch (SQLException e) { System.err.println("GemiDAO hata: " + e.getMessage()); }
        return liste;
    }

    public void tumunuKaydet(List<Gemi> liste) {
        for (Gemi g : liste) kaydet(g);
    }
}