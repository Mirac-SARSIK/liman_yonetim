package liman.veri;

import liman.model.Rihtim;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RihtimDAO {

    public List<Rihtim> hepsiniGetir() {
        List<Rihtim> liste = new ArrayList<>();
        String sql = "SELECT * FROM rihtim";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Rihtim r = new Rihtim(
                    rs.getString("rihtim_id"),
                    rs.getDouble("uzunluk"),
                    rs.getDouble("derinlik"),
                    rs.getInt("vinc_sayisi")
                );
                r.setDurum(Rihtim.Durum.valueOf(rs.getString("durum")));
                r.setBaglıGemiId(rs.getString("bagli_gemi_id"));
                liste.add(r);
            }
        } catch (SQLException e) { System.err.println("RihtimDAO hata: " + e.getMessage()); }
        return liste;
    }

    public void kaydet(Rihtim r) {
        String sql = "INSERT INTO rihtim VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                   + "uzunluk=?, derinlik=?, vinc_sayisi=?, durum=?, bagli_gemi_id=?";
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getRihtimId());
            ps.setDouble(2, r.getUzunluk());
            ps.setDouble(3, r.getDerinlik());
            ps.setInt(4, r.getVincSayisi());
            ps.setString(5, r.getDurum().name());
            ps.setString(6, r.getBaglıGemiId());
            ps.setDouble(7, r.getUzunluk());
            ps.setDouble(8, r.getDerinlik());
            ps.setInt(9, r.getVincSayisi());
            ps.setString(10, r.getDurum().name());
            ps.setString(11, r.getBaglıGemiId());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("RihtimDAO kaydet hata: " + e.getMessage()); }
    }

    public void sil(String rihtimId) {
        try (Connection c = VeriTabaniBaglantisi.getBaglanti();
             PreparedStatement ps = c.prepareStatement("DELETE FROM rihtim WHERE rihtim_id=?")) {
            ps.setString(1, rihtimId);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("RihtimDAO sil hata: " + e.getMessage()); }
    }

    public void tumunuKaydet(List<Rihtim> liste) {
        for (Rihtim r : liste) kaydet(r);
    }
}