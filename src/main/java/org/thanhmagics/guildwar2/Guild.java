package org.thanhmagics.guildwar2;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guild implements Serializable {

    public String leader;

    public List<String> members = new ArrayList<>();

    public String name;

    public int kill,camped;


//    public int getPoint() {
//        return kill + (camped * 3);
//    }
    public int point;


    public void sendMessage(String message) {
        for (String uuid : members) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if (!op.isOnline())
                continue;
            ((Player)op).sendMessage(CCConfig.applyColor(message));
        }
    }

    public Guild clone() {
        Guild guild = new Guild();
        guild.leader = leader;
        guild.members = members;
        guild.name = name;
        guild.kill = kill;
        guild.camped = camped;
        guild.point = point;
        return guild;
    }



}
