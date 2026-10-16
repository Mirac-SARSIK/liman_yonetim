package liman.util;

import java.util.Random;

public class IdUretici {

    private static Random random = new Random();
    private static int gemiSayaci = 1;
    private static int konteynerSayaci = 1;

    public static String gemiIdUret() {
        return String.format("GEMI-%03d", gemiSayaci++);
    }

    public static String konteynerIdUret() {
        String harfler = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sahipKodu = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sahipKodu.append(harfler.charAt(random.nextInt(harfler.length())));
        }
        sahipKodu.append("U");

        int seriNo = 1000000 + konteynerSayaci++;
        return sahipKodu.toString() + seriNo;
    }
}
