package org.thanhmagics.guildwar2;

import org.bukkit.OfflinePlayer;
import java.util.HashMap;
import java.util.Map;

public class Cooldown implements java.lang.Runnable {

    private static Map<Long, Runnable> cd1 = new HashMap<>();

    private static Map<OfflinePlayer,Long> cd2 = new HashMap<>();


    public static void addPlayer(OfflinePlayer player, Runnable runnable) {
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
        new Thread(() -> {
            while (!GuildWar2.get().getServer().isStopping()) {
                for (Long k : cd1.keySet()) {
                    if (k + 10000 < System.currentTimeMillis()) {
                        Runnable runnable = cd1.get(k);
                        cd1.remove(k, runnable);
                        OfflinePlayer player = kbv(k);
                        cd2.remove(player);
                        runnable.run(player);
                    }
                }
            }
        }).start();
    }

    public static OfflinePlayer kbv(Long v) {
        for (OfflinePlayer p : cd2.keySet())
            if (cd2.get(p).equals(v))
                return p;
        return null;
    }
    public abstract static class Runnable {
        public abstract void run(OfflinePlayer player);
    }
}
