package org.thanhmagics.guildwar2;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cooldown implements java.lang.Runnable {

    private static Map<Long, java.lang.Runnable> cd1 = new HashMap<>();

    private static Map<OfflinePlayer,Long> cd2 = new HashMap<>();


    public static void addPlayer(OfflinePlayer player, java.lang.Runnable runnable) {
        long ctm = System.currentTimeMillis();
        cd1.put(ctm,runnable);
        cd2.put(player,ctm);
    }

    public static void removePlayer(OfflinePlayer player) {
        if (cd2.containsKey(player)) {
            long value = cd2.get(player);
            cd1.remove(value);
            cd2.remove(player,value);
        }
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            for (Long k : cd1.keySet()) {
                if (k + 60000 < System.currentTimeMillis()) {
                    java.lang.Runnable runnable = cd1.get(k);
                    cd1.remove(k,runnable);
                    cd2.remove(kbv(k));
                    runnable.run();
                }
            }
        });
        executorService.shutdown();
    }

    OfflinePlayer kbv(Long v) {
        for (OfflinePlayer p : cd2.keySet())
            if (cd2.get(p).equals(v))
                return p;
        return null;
    }

}
