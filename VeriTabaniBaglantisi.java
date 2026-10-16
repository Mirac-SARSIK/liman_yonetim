package liman.veri;

import java.sql.*;

public class VeriTabaniBaglantisi {

    private static final String HOST    = "localhost";
    private static final String PORT    = "3306";
    private static final String DB_ADI  = "liman_db";
    private static final String ROOT    = "root";
    
    private static String SIFRE = System.getProperty("db.sifre",
                                  System.getenv("MYSQL_SIFRE") != null
                                  ? System.getenv("MYSQL_SIFRE") : "liman123");

    private static Connection baglanti = null;

    public static void setSifre(String sifre) {
        SIFRE = sifre;
        baglanti = null; 
    }

    public static Connection getBaglanti() {
        try {
            if (baglanti == null || baglanti.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                baglanti = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_ADI
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                    + "&characterEncoding=UTF-8",
                    ROOT, SIFRE
                );
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL sürücüsü bulunamadı!");
        } catch (SQLException e) {
            System.err.println("Bağlantı hatası: " + e.getMessage());
        }
        return baglanti;
    }

    
    public static Connection getRootBaglantisi() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL sürücüsü bulunamadı!");
        }
        return DriverManager.getConnection(
            "jdbc:mysql://" + HOST + ":" + PORT
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
            + "&characterEncoding=UTF-8",
            ROOT, SIFRE
        );
    }

    
    public static boolean veritabaniKur() {
        try (Connection root = getRootBaglantisi();
             Statement st = root.createStatement()) {

            st.executeUpdate(
                "CREATE DATABASE IF NOT EXISTS " + DB_ADI
                + " CHARACTER SET utf8 COLLATE utf8_turkish_ci"
            );
            System.out.println("Veritabanı hazır.");

        } catch (SQLException e) {
            System.err.println("DB oluşturulamadı: " + e.getMessage());
            return false;
        }

        try (Connection c = getBaglanti();
             Statement st = c.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS rihtim (" +
                "  rihtim_id     VARCHAR(50)  PRIMARY KEY," +
                "  uzunluk       DOUBLE       NOT NULL," +
                "  derinlik      DOUBLE       NOT NULL," +
                "  vinc_sayisi   INT          NOT NULL," +
                "  durum         VARCHAR(30)  DEFAULT 'BOS'," +
                "  bagli_gemi_id VARCHAR(50)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS blok (" +
                "  blok_id   VARCHAR(50) PRIMARY KEY," +
                "  tip       VARCHAR(30) NOT NULL," +
                "  max_sira  INT         NOT NULL," +
                "  max_yigin INT         NOT NULL," +
                "  max_kat   INT         NOT NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS gemi (" +
                "  gemi_id         VARCHAR(50)  PRIMARY KEY," +
                "  gemi_adi        VARCHAR(100) NOT NULL," +
                "  uzunluk         DOUBLE," +
                "  genislik        DOUBLE," +
                "  su_cekimi       DOUBLE," +
                "  kapasite_teu    INT," +
                "  durum           VARCHAR(30)," +
                "  bagli_rihtim_id VARCHAR(50)," +
                "  FOREIGN KEY (bagli_rihtim_id)" +
                "      REFERENCES rihtim(rihtim_id) ON DELETE SET NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS konteyner (" +
                "  konteyner_id VARCHAR(20) PRIMARY KEY," +
                "  tip          VARCHAR(30)," +
                "  yon          VARCHAR(20)," +
                "  dolu         BOOLEAN," +
                "  durum        VARCHAR(30)," +
                "  gemi_id      VARCHAR(50)," +
                "  blok_id      VARCHAR(50)," +
                "  sira         INT," +
                "  yigin        INT," +
                "  kat          INT," +
                "  tir_plaka    VARCHAR(20)," +
                "  FOREIGN KEY (gemi_id) REFERENCES gemi(gemi_id)     ON DELETE SET NULL," +
                "  FOREIGN KEY (blok_id) REFERENCES blok(blok_id)     ON DELETE SET NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tir (" +
                "  plaka                 VARCHAR(20) PRIMARY KEY," +
                "  sofor_adi             VARCHAR(100)," +
                "  durum                 VARCHAR(30)," +
                "  tasidigi_konteyner_id VARCHAR(20)," +
                "  kalan_islem_saniyesi  INT DEFAULT 0" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS kullanici (" +
                "  kullanici_adi VARCHAR(50)  PRIMARY KEY," +
                "  sifre         VARCHAR(100) NOT NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS olay_gunlugu (" +
                "  id     INT AUTO_INCREMENT PRIMARY KEY," +
                "  kaynak VARCHAR(50)," +
                "  mesaj  TEXT," +
                "  zaman  TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Varsayılan kullanıcı — zaten varsa tekrar ekleme
            st.executeUpdate(
                "INSERT IGNORE INTO kullanici VALUES ('Amiral', '456')"
            );

            System.out.println("Tüm tablolar hazır.");
            return true;

        } catch (SQLException e) {
            System.err.println("Tablo oluşturma hatası: " + e.getMessage());
            return false;
        }
    }

    public static void baglantiKapat() {
        try {
            if (baglanti != null && !baglanti.isClosed()) {
                baglanti.close();
            }
        } catch (SQLException ignored) {}
    }
}