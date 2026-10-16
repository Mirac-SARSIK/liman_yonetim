package liman.ui;

import liman.model.*;
import liman.servis.LimanSaati;
import liman.veri.LimanVeri;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LimanHaritasiPanel extends JPanel {

    private LimanVeri veri = LimanVeri.getInstance();

    public LimanHaritasiPanel() {
        setBackground(new Color(173, 216, 230));

        LimanSaati.getInstance().dinleyiciEkle(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        rihtimlariCiz(g2);
        bloklariCiz(g2);
        kapiKuyruğunuCiz(g2);
    }

    private void rihtimlariCiz(Graphics2D g2) {
        List<Rihtim> rihtimlar = veri.getRihtimlar();
        int x = 30, y = 30, genislik = 180, yukseklik = 70, bosluk = 15;

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));

        for (Rihtim r : rihtimlar) {

            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(x, y, genislik, yukseklik);

            if (r.getDurum() == Rihtim.Durum.MESGUL && r.getBaglıGemiId() != null) {
                Gemi gemi = gemiBul(r.getBaglıGemiId());
                Color gemiRengi = gemi != null && gemi.getDurum() == Gemi.Durum.BOSALTILIYOR
                        ? new Color(230, 126, 34)
                        : new Color(52, 73, 94);

                g2.setColor(gemiRengi);
                g2.fillRoundRect(x + 15, y + 10, genislik - 30, yukseklik - 20, 10, 10);

                g2.setColor(Color.WHITE);
                if (gemi != null) {
                    g2.drawString(gemi.getGemiAdi(), x + 20, y + yukseklik / 2 + 5);
                }
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString("BOŞ", x + genislik / 2 - 15, y + yukseklik / 2 + 5);
            }

            g2.setColor(Color.BLACK);
            g2.drawString(r.getRihtimId(), x, y - 5);

            y += yukseklik + bosluk;
        }
    }

    private void bloklariCiz(Graphics2D g2) {
        List<Blok> bloklar = veri.getBloklar();
        int xBaslangic = 280, y = 30, genislik = 140, yukseklik = 90, bosluk = 15;
        int x = xBaslangic;
        int sutunBasiSayisi = 0;

        for (Blok b : bloklar) {
            int kapasite = b.getMaxSira() * b.getMaxYigin() * b.getMaxKat();
            int dolu = b.getIcindekiKonteynerler().size();
            double doluOrani = kapasite == 0 ? 0 : (double) dolu / kapasite;

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, genislik, yukseklik);

            int doluYukseklik = (int) (yukseklik * doluOrani);
            g2.setColor(blokRengi(b.getTip()));
            g2.fillRect(x, y + (yukseklik - doluYukseklik), genislik, doluYukseklik);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString(b.getBlokId(), x, y - 5);
            g2.drawString(dolu + "/" + kapasite, x + 5, y + 15);

            x += genislik + bosluk;
            sutunBasiSayisi++;
            if (sutunBasiSayisi % 2 == 0) {
                x = xBaslangic;
                y += yukseklik + bosluk + 20;
            }
        }
    }

    private void kapiKuyruğunuCiz(Graphics2D g2) {
        List<Tir> tirlar = veri.getTirlar();
        int x = 30, y = 400;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("Gate / Kapı Trafiği:", x, y);

        y += 20;
        for (Tir t : tirlar) {
            if (t.getDurum() == Tir.Durum.KAPIDA_BEKLIYOR) {
                g2.setColor(new Color(241, 196, 15)); // sarı: bekliyor
            } else if (t.getDurum() == Tir.Durum.GOREVLI) {
                g2.setColor(new Color(46, 204, 113)); // yeşil: işlem yapılıyor
            } else {
                continue;
            }

            g2.fillRoundRect(x, y, 18, 18, 6, 6);
            g2.setColor(Color.BLACK);
            g2.drawString(t.getPlaka() + " -> " + t.getTasidigiKonteynerId() + " [" + t.getDurum() + "]", x + 25, y + 14);
            y += 22;
        }
    }

    private Color blokRengi(Blok.BlokTipi tip) {
        switch (tip) {
            case ITHALAT:
                return new Color(52, 152, 219);
            case IHRACAT:
                return new Color(155, 89, 182);
            case REEFER:
                return new Color(26, 188, 156);
            case BOS_KONTEYNER:
                return new Color(149, 165, 166);
            default:
                return Color.GRAY;
        }
    }

    private Gemi gemiBul(String gemiId) {
        for (Gemi g : veri.getGemiler()) {
            if (g.getGemiId().equals(gemiId)) {
                return g;
            }
        }
        return null;
    }
}
