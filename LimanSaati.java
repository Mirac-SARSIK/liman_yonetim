package liman.servis;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public class LimanSaati {

    private static LimanSaati instance;

    private Timer timer;
    private List<Runnable> dinleyiciler = new ArrayList<>();
    private long gecenSaniye = 0;
    private boolean calisiyorMu = false;

    private LimanSaati() {

        timer = new Timer(1000, e -> tikAt());
    }

    public static LimanSaati getInstance() {
        if (instance == null) {
            instance = new LimanSaati();
        }
        return instance;
    }

    public void baslat() {
        if (!calisiyorMu) {
            timer.start();
            calisiyorMu = true;
        }
    }

    public void durdur() {
        timer.stop();
        calisiyorMu = false;
    }

    private void tikAt() {
        gecenSaniye++;
        for (Runnable r : new ArrayList<>(dinleyiciler)) {
            try {
                r.run();
            } catch (Exception ex) {
                System.err.println("Saat dinleyicisinde hata: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void dinleyiciEkle(Runnable r) {
        dinleyiciler.add(r);
    }

    public void dinleyiciSil(Runnable r) {
        dinleyiciler.remove(r);
    }

    public long getGecenSaniye() {
        return gecenSaniye;
    }
}
