package org.thanhmagics.guildwar2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CCConfig {

    private FileConfiguration config;


    public String arena,start,end,chat,war_mess1,war_mess2,war_mess3;

    public World world;

    public List<String> helpCommand = new ArrayList<>();
    public int max_member;

    public List<Camp> camps = new ArrayList<>();

    public List<String> guild_message = new ArrayList<>();

    public List<String> reward = new ArrayList<>();

    public List<String> message2 = new ArrayList<>();

    public List<String> message3 = new ArrayList<>();

    public List<String> message4 = new ArrayList<>();

    public List<String> message5 = new ArrayList<>();

    public List<String> message6 = new ArrayList<>();
    public List<String> message7 = new ArrayList<>();
    public List<String> message8 = new ArrayList<>();
    public List<String> message9 = new ArrayList<>();

    public List<String> message10 = new ArrayList<>();

    public CCConfig(FileConfiguration config) {
        this.config = config;
        war_mess1 = applyColor(config.getString("war.message1"));
        war_mess2 = applyColor(config.getString("war.message2"));
        war_mess3 = applyColor(config.getString("war.message3"));
        arena = config.getString("war.arena");
        world = Bukkit.getWorld(config.getString("war.world"));
        start = config.getString("war.start");
        end = config.getString("war.end");
        chat = applyColor(config.getString("chat"));
        max_member = config.getInt("guild.max_member");
        helpCommand.addAll(applyColor(config.getStringList("command.help")));
        guild_message.addAll(applyColor(config.getStringList("guild.message1")));
        reward.addAll(applyColor(config.getStringList("war.reward")));
        message2.addAll(applyColor(config.getStringList("guild.message2")));
        message3.addAll(applyColor(config.getStringList("guild.message3")));
        message4.addAll(applyColor(config.getStringList("guild.message4")));
        message5.addAll(applyColor(config.getStringList("guild.message5")));
        message6.addAll(applyColor(config.getStringList("guild.message6")));
        message7.addAll(applyColor(config.getStringList("guild.message7")));
        message8.addAll(applyColor(config.getStringList("guild.message8")));
        message9.addAll(applyColor(config.getStringList("guild.message9")));
        message10.addAll(applyColor(config.getStringList("war.message4")));
        try {
            for (String k : config.getConfigurationSection("war.camp").getKeys(false)) {
                Camp camp = new Camp();
                camp.start = string2location(config.getString("war.camp." + k + ".location").split(";")[0]);
                camp.end = string2location(config.getString("war.camp." + k + ".location").split(";")[1]);
                camp.title_location = string2location(config.getString("war.camp." + k + ".title_location"));
                camp.time = (int) (config.getInt("war.camp." + k + ".time"));
                camp.title = applyColor(config.getStringList("war.camp." + k + ".title"));
                camps.add(camp);
            }
        } catch (Exception e) {}
    }

    public Location string2location(String input) {
        return new Location(world,Double.parseDouble(input.split(",")[0]),
                Double.parseDouble(input.split(",")[1]),
                Double.parseDouble(input.split(",")[2]));
    }

    public Location[] arenaLocation() {
        try {
            Location[] rs = new Location[2];
            rs[0] = new Location(world, Double.parseDouble(arena.split(";")[0].split(",")[0]),
                    Double.parseDouble(arena.split(";")[0].split(",")[1]),
                    Double.parseDouble(arena.split(";")[0].split(",")[2]));
            rs[1] = new Location(world, Double.parseDouble(arena.split(";")[1].split(",")[0]),
                    Double.parseDouble(arena.split(";")[1].split(",")[1]),
                    Double.parseDouble(arena.split(";")[1].split(",")[2]));
            return rs;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public long string2time(String str) {
        long rs =0;
        if (str.contains("h")) {
            rs += Long.parseLong(str.split("h")[0]) * (60000 * 60);
        }
        if (str.contains("p")) {
            if (str.contains("h")) {
                rs += Long.parseLong(str.split("h")[1].split("p")[0]) * 60000;
            } else {
                rs +=  Long.parseLong(str.split("p")[0]) * 60000;
            }
        }
        return rs;
    }


    static String applyColor(String str) {
        return ChatColor.translateAlternateColorCodes('&',str);
    }
    static List<String> applyColor(List<String> strings) {
        List<String> rs = new ArrayList<>();
        for (String s : strings) {
            rs.add(applyColor(s));
        }
        return rs;
    }
}
