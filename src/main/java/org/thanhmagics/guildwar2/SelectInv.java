package org.thanhmagics.guildwar2;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.thanhmagics.guildwar2.CCConfig.applyColor;

public class SelectInv implements Listener {

    static Map<String,Runnable> uuids = new HashMap<>();
    public static void open(Player player,Runnable r) {
        UUID uuid = UUID.randomUUID();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        FileConfiguration config = GuildWar2.get().getConfig();
        //int size = 9 + (pPlayer.getGuild().members.size() / 11) * 9;
        Inventory inventory = Bukkit.createInventory(null,9*3,applyColor(config.getString("gui.select.title")));
        for (String uid : pPlayer.getGuild().members) {
            if (uid.equals(pPlayer.getGuild().leader))
                continue;
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uid));
            ItemStack itemStack = GuildWar2.get().itemFromConfig("gui.select.player",config);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("{player_name}",op.getName()));
            itemStack.setItemMeta(meta);
            inventory.setItem(inventory.firstEmpty(),itemStack);
        }
        pPlayer.inventory = uuid.toString();
        uuids.put(uuid.toString(),r);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        if (pPlayer.inventory == null)
            return;
        if (uuids.containsKey(pPlayer.inventory)) {
            event.setCancelled(true);
            if (!(event.getClickedInventory() instanceof PlayerInventory)) {
                List<String> members = new ArrayList<>(pPlayer.getGuild().members);
                members.remove(pPlayer.getGuild().leader);
                uuids.get(pPlayer.inventory).run(members.get(event.getSlot()));
                player.closeInventory();
            }
        }
    }
}
abstract class Runnable {

    public abstract void run(String uid);


}