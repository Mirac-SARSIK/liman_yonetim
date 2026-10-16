package liman.veri;

import liman.model.Blok;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlokDAO {

    public List<Blok> hepsiniGetir() {
        List<Blok> liste = new ArrayList<>();
        String sql = "SELECT * FROM blok";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Blok(
                    rs.getString("blok_id"),
                    Blok.BlokTipi.valueOf(rs.getString("tip")),
                    rs.getInt("max_sira"),
                    rs.getInt("max_yigin"),
                    rs.getInt("max_kat")
                ));
            }
        } catch (SQLException e) { System.err.println("BlokDAO hata: " + e.getMessage()); }
        return liste;
    }

    public void kaydet(Blok b) {
        String sql = "INSERT INTO blok VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                   + "tip=?, max_sira=?, max_yigin=?, max_kat=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getBlokId());
            ps.setString(2, b.getTip().name());
            ps.setInt(3, b.getMaxSira());
            ps.setInt(4, b.getMaxYigin());
            ps.setInt(5, b.getMaxKat());
            ps.setString(6, b.getTip().name());
            ps.setInt(7, b.getMaxSira());
            ps.setInt(8, b.getMaxYigin());
            ps.setInt(9, b.getMaxKat());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("BlokDAO kaydet hata: " + e.getMessage()); }
    }

public void sil(String blokId) {
    try (Connection c = VeriTabaniBaglantisi.getBaglanti();
         PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM blok WHERE blok_id=?")) {
        ps.setString(1, blokId);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("BlokDAO sil hata: " + e.getMessage());
    }
}

    public void tumunuKaydet(List<Blok> liste) {
        for (Blok b : liste) kaydet(b);
    }
}