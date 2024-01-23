package org.thanhmagics.guildwar2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class War implements java.lang.Runnable {

    public static War instance = null;

    public static void countdown() {
        GuildWar2.get().resetTime();
        long end = System.currentTimeMillis() + (60000 * 10);
        List<Long> t = new ArrayList<>();
        t.add(System.currentTimeMillis());
        t.add(System.currentTimeMillis() + (600000 / 2));
        int t1 = (60000 * 9);
        t.add(System.currentTimeMillis() + t1);
        t.add(System.currentTimeMillis() + (t1 + (1000 * 30)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 50)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 55)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 56)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 57)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 58)));
        t.add(System.currentTimeMillis() + (t1 + (1000 * 59)));
        new BukkitRunnable() {
            int anInt = 0;
            @Override
            public void run() {
                if (System.currentTimeMillis() >= end) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (String s : GuildWar2.get().config.message10) {
                            player.sendMessage(CCConfig.applyColor(s));
                        }
                    }
                    this.cancel();
                    if (instance == null)
                        start();
                    else
                        throw new RuntimeException("đã có 1 war đã tồn tại trước đó @@!!");
                } else {
                    long l = t.get(anInt);
                    if (l <= System.currentTimeMillis()) {
                        anInt++;
                        long t = end - l;
                        int t1 = (int) (t / 1000);
                        int t2 = (t1 / 60);
                        t1 = t1 % 60;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(CCConfig.applyColor(
                                    GuildWar2.get().config.war_mess3.replace("{time}",t2 + " phút " + t1 + " giây")
                            ));
                        }
                    }
                }
            }
        }.runTaskTimer(GuildWar2.get(),0,20);
    }
    public static boolean start() {
        if (instance != null) {
            System.out.println("Đã Có War Tồn Tại!");
            return false;
        }
        War war = new War();
        for (int i = 0; i < GuildWar2.get().config.camps.size(); i++) {
            WarCamp warCamp = new WarCamp(i);
            warCamp.camp = GuildWar2.get().config.camps.get(i);
            war.camps.put(i,warCamp);
        }
        war.run();
        return true;
    }

    final Map<Integer,WarCamp> camps = new HashMap<>();
    List<ArmorStand> titles = new ArrayList<>();

    public List<Player> isp = new ArrayList<>();

    public Map<Player,Integer> kill = new HashMap<>();
    public boolean endwar = false;

    public boolean end = false;

    public void end() {
        War.instance = null;
        int rpc = (GuildWar2.get().getConfig().getInt("war.reward_point.camp"));
        int rpk = (GuildWar2.get().getConfig().getInt("war.reward_point.kill"));
            for (ArmorStand ars : titles) {
                ars.remove();
            }
            for (WarCamp camp : camps.values()) {
                if (camp.getGuild() != null && GuildWar2.get().dataStorage.guildStorage.containsKey(camp.getGuild())) {
                    Guild winner = GuildWar2.get().dataStorage.guildStorage.get(camp.getGuild());
                    winner.camped = winner.camped + 1;
                    winner.point = winner.point + rpc;
                }
            }
        Map<String,Integer> camped = new HashMap<>();
        for (WarCamp camp : camps.values()) {
            if (!Objects.equals(camp.getGuild(), "&cCHƯA CÓ AI")) {
                if (camped.containsKey(camp.getGuild())) {
                    Integer oldValue = camped.get(camp.getGuild());
                    camped.remove(camp.getGuild(),oldValue);
                    camped.put(camp.getGuild(),oldValue + rpc);
                } else {
                    camped.put(camp.getGuild(),rpc);
                }
            }
        }
        for (Player player : kill.keySet()) {
            PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
            Integer integer = kill.get(player);
            Integer oldPoint = camped.get(pPlayer.guild);
            camped.remove(pPlayer.guild,oldPoint);
            camped.put(pPlayer.guild,oldPoint + (integer * rpk));
        }
        Map<String,Integer> top = new HashMap<>();
      //  List<String> topp = new ArrayList<>();
        while (camped.size() != 0) {
            int i = 0;
            String str = null;
            for (String k : camped.keySet()) {
                Integer v = camped.get(k);
                if (v >= i) {
                    i = v;
                    str = k;
                }
            }
            if (i != 0) {
                top.put(str, i);
                camped.remove(str,i);
            }
        }
//        for (String k : top.keySet()) {
//            Integer v = top.get(k);
//            Bukkit.getPlayer("ongearzz").sendMessage("k: " +k);
//            Bukkit.getPlayer("ongearzz").sendMessage("v: " +v);
//            Bukkit.getPlayer("ongearzz").sendMessage(" - ");
//        }

//        while (top.size() <= Math.min(camped.size(), 3)) {
//            int largest = 0;
//            String key = null;
//            Iterator<Map.Entry<String, Integer>> iterator = camped.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, Integer> entry = iterator.next();
//                Integer v = entry.getValue();
//                if (largest <= v) {
//                    key = entry.getKey();
//                    largest = v;
//                    iterator.remove();
//                }
//            }
//
//            if (key != null) {
//                top.put(key, largest);
//                topp.add(key);
//            }
//        }
        List<String> ks = new ArrayList<>(top.keySet());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int i = 0;
            for (String str : GuildWar2.get().config.reward) {
                if (str.contains("{guild_name}") && str.contains("{point}")) {
                    String key,point;
                    if (top.size() - 1 >= i) {
                        key = ks.get(i);
                        point = String.valueOf(top.get(key));
                    } else {
                        key = " - ";
                        point = "-";
                    }
                    str = str.replace("{guild_name}",key).replace("{point}",point);
                    onlinePlayer.sendMessage(CCConfig.applyColor(str));
                    i++;
                } else {
                    onlinePlayer.sendMessage(str);
                }
            }
        }
    }

    @Override
    public void run() {
        CCConfig config = GuildWar2.get().config;
        Location[] arena = config.arenaLocation();
        if (arena == null)
            throw new RuntimeException("error: config.arenaLocation();");
        War.instance = this;
        long endTime = Long.parseLong(config.end) * 60000;
        Map<Player,Integer> camping = new HashMap<>();
        Map<ArmorStand,String[]> ar = new HashMap<>();
        List<Player> message2 = new ArrayList<>();
        for (WarCamp warCamp : camps.values()) {
            int i = 0;
            for (String title : warCamp.camp.title) {
                ArmorStand ars = hologram("",warCamp.camp.title_location.clone().subtract(0,0.4 * i,0));
                if (title.contains("{guild}"))
                    ar.put(ars,new String[] {String.valueOf(warCamp.id),warCamp.getGuild(),title});
                ars.setCustomName(CCConfig.applyColor(title.replace("{guild}",warCamp.getGuild())));
                i++;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (endwar) {
                    end();
                    this.cancel();
                }
                for (Player player : camping.keySet()) {
                    int pt = camping.get(player);
                    player.sendMessage(config.war_mess1.replace("{progress}",String.valueOf(pt)));
                }
                Iterator<Player> iterator = message2.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    player.sendMessage(config.war_mess2);
                    iterator.remove();
                }
                for (ArmorStand armorStand : ar.keySet()) {
                    String[] value = ar.get(armorStand);
                    WarCamp warCamp = camps.get(Integer.parseInt(value[0]));
                    if (!value[1].equals(warCamp.getGuild())) {
                        String[] oldValue = value.clone();
                        armorStand.setCustomName(CCConfig.applyColor(value[2].replace("{guild}",warCamp.getGuild())));
                        value[1] = warCamp.getGuild();
                        ar.replace(armorStand,oldValue,value);
                    }
                }
            }
        }.runTaskTimer(GuildWar2.get(),0,20);
        new Thread(() -> {
            List<Player> o = new ArrayList<>();
            List<Player> n;
            long start = System.currentTimeMillis();
            Map<Player,Long> time = new HashMap<>();
            while (!end) {
                n = new ArrayList<>();
                List<Player> inside = new ArrayList<>();
                for (Player player : config.world.getPlayers()) {
                    if (inside(player.getLocation(),arena[0],arena[1]))
                        inside.add(player);
                }
                isp = inside;
                Map<Player,WarCamp> playerCampMap = new HashMap<>();
                List<WarCamp> ignored = new ArrayList<>();
                for (Player player : inside) {
                    for (WarCamp camp : camps.values()) {
                        if (inside(player.getLocation(),camp.camp.start,camp.camp.end)) {
                            if (!playerCampMap.containsValue(camp)) {
                                playerCampMap.put(player, camp);
                            } else {
                                ignored.add(camp);
                            }
                        }
                    }
                }
                for (Player key : playerCampMap.keySet())
                    if (ignored.contains(playerCampMap.get(key)))
                        playerCampMap.remove(key);
                for (Player key : playerCampMap.keySet())
                    if (GuildWar2.get().dataStorage.playerStorage.get(key.getUniqueId().toString()).guild == null)
                        playerCampMap.remove(key);
                for (Player player : playerCampMap.keySet()) {
                    WarCamp camp = playerCampMap.get(player);
                    PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
                    if (pPlayer.guild != null && pPlayer.guild.equals(camp.getGuild()))
                        continue;
                    if (!time.containsKey(player)) {
                        time.put(player,System.currentTimeMillis());
                      //  continue;
                    }
                    long t = time.get(player);
                    if (System.currentTimeMillis() - t> camp.camp.time) {
                        time.remove(player);
                        camping.remove(player);
                        message2.add(player);
                        camp.setGuild(pPlayer.guild);
                    } else {
                        if (camping.containsKey(player)) {
                            int old = camping.get(player);
                            camping.replace(player,old, (int) ((System.currentTimeMillis() - t) * 100 / camp.camp.time));
                        } else {
                            camping.put(player,1);
                        }
                    }
                    n.add(player);
                }
                for (Player op : o) {
                    if (!n.contains(op)) {
                        time.remove(op);
                        camping.remove(op);
                    }
                }
                o.clear();
                o.addAll(n);
//                for (Player key : time.keySet())
//                    if (!inside.contains(key)) {
//                        time.remove(key);
//                        camping.remove(key);
//                    }

                if (System.currentTimeMillis() - start > endTime) {
                    end = true;
                    endwar = true;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    boolean inside(Location l1,Location l2,Location l3) {
        return  (l1.getBlockX() >= l2.getBlockX() && l1.getBlockX() <= l3.getBlockX() &&
                l1.getBlockZ() >= l2.getBlockZ() && l1.getBlockZ() <= l3.getBlockZ());
    }
    ArmorStand hologram(String title,Location location) {
        ArmorStand ars = location.getWorld().spawn(location,ArmorStand.class);
        ars.setCustomNameVisible(true);
        ars.setGravity(false);
        ars.setVisible(false);
        ars.setInvulnerable(true);
        ars.setCustomName(CCConfig.applyColor(title));
        titles.add(ars);
        return ars;
    }

}


class WarCamp {

    public int id;
    public Camp camp;

    private String guild = "&cCHƯA CÓ AI";

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        if (guild != null) {
            this.guild = guild;
        }
    }

    public WarCamp(int id) {
        this.id = id;
    }
}

























