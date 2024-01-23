package org.thanhmagics.guildwar2;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class Listeners implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        PPlayer pPlayer = (GuildWar2.get().dataStorage.playerStorage.get(event.getPlayer().getUniqueId().toString()));
        ListGuildInv.uuids.remove(pPlayer.inventory);
        SelectInv.uuids.remove(pPlayer.inventory);
        SettingInv.uuids.remove(pPlayer.inventory);
        pPlayer.inventory = null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!GuildWar2.get().dataStorage.playerStorage.containsKey(player.getUniqueId().toString())) {
            PPlayer pPlayer = new PPlayer(player.getUniqueId().toString());
            GuildWar2.get().dataStorage.playerStorage.put(player.getUniqueId().toString(),pPlayer);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() == null)
            return;
        Player killer = player.getKiller();
        PPlayer killerPPlayer = GuildWar2.get().dataStorage.playerStorage.get(killer.getUniqueId().toString());
        if (player.getWorld().equals(GuildWar2.get().config.world)) {
            if (killerPPlayer.guild != null && War.instance != null && War.instance.isp.contains(player) && War.instance.isp.contains(killer)) {
                Guild guild = killerPPlayer.getGuild();
                guild.kill = guild.kill + 1;
                guild.point = guild.point + 1;
                Integer oldInt = War.instance.kill.get(killer);
                War.instance.kill.remove(killer,oldInt);
                War.instance.kill.put(killer,oldInt+1);
            }
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        if (pPlayer.chat && pPlayer.guild != null) {
            event.setCancelled(true);
            String message = GuildWar2.get().config.chat;
            for (String member : pPlayer.getGuild().members) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(member));
                if (!op.isOnline())
                    continue;
                ((Player)op).sendMessage(message.replace("{name}",player.getName()).replace("{chat}",event.getMessage()));
            }
        }
    }
}
