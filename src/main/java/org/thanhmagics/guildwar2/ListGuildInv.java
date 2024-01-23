package org.thanhmagics.guildwar2;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import static org.thanhmagics.guildwar2.CCConfig.applyColor;

import java.util.*;

public class ListGuildInv implements Listener {

    static List<String> uuids = new ArrayList<>();

    static Map<Player,Integer> pageStorage = new HashMap<>();
    static int[] ints = new int[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    public static void open(Player player) {
        if (!pageStorage.containsKey(player))
            pageStorage.put(player,1);
        UUID uuid = UUID.randomUUID();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        FileConfiguration config = GuildWar2.get().getConfig();
        Inventory inventory = Bukkit.createInventory(null,54,applyColor(config.getString("gui.list.title")));
        ItemStack deco = GuildWar2.get().itemFromConfig("gui.list.deco",config);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (contains(i))
                continue;
            inventory.setItem(i,deco);
        }
        inventory.setItem(18,GuildWar2.get().itemFromConfig("gui.list.previous_page",config));
        inventory.setItem(26,GuildWar2.get().itemFromConfig("gui.list.next_page",config));
        inventory.setItem(40,GuildWar2.get().itemFromConfig("gui.list.info",config));
        List<Guild> guilds = new ArrayList<>(GuildWar2.get().dataStorage.guildStorage.values());
        int page = pageStorage.get(player);
        if (guilds.size() < (21 * (page-1)))
            page=page-1;
        for (int i = 21 * (page - 1); i < guilds.size(); i++) {
            Guild guild = guilds.get(i);
            ItemStack itemStack = GuildWar2.get().itemFromConfig("gui.list.guild",config).clone();
            ItemMeta meta = itemStack.getItemMeta().clone();
            meta.setDisplayName(meta.getDisplayName().replace("{guild_name}", applyColor(guild.name)));
            List<String> lore = new ArrayList<>();
            for (String str : meta.getLore()) {
                str = str.replace("{guild_kill}", String.valueOf(guild.kill)).replace("{guild_camped}", String.valueOf(guild.camped))
                        .replace("{guild_point}",String.valueOf(guild.point));
                if (str.contains("{guild_member}")) {
                    for (String member : guild.members) {
                        String strClone = new String(str);
                        strClone = strClone.replace("{guild_member}", Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(member)).getName())) +
                        (guild.leader.equals(member) ? " &e(Đội Trưởng)" : "");
                        lore.add(applyColor(strClone));
                    }
                } else if (str.contains("{/}")) {
                    if (guild.members.contains(player.getUniqueId().toString()) || pPlayer.guild != null) {
                        lore.add(applyColor(str.split("\\{/}")[2]));
                    } else {
                        if (guild.members.size() >= GuildWar2.get().config.max_member) {
                            lore.add(applyColor(str.split("\\{/}")[1]));
                        } else {
                            lore.add(str.split("\\{/}")[0]);
                        }
                    }
                } else {
                    lore.add(str);
                }
            }
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            inventory.setItem(inventory.firstEmpty(),itemStack);
        }
        pPlayer.inventory = uuid.toString();
        uuids.add(uuid.toString());
        player.openInventory(inventory);
    }

    static boolean contains(int i) {
        for (int j : ints)
            if (j == i)
                return true;
        return false;
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        if (pPlayer.inventory == null)
            return;
        if (uuids.contains(pPlayer.inventory)) {
            event.setCancelled(true);
            if (!(event.getClickedInventory() instanceof PlayerInventory)) {
                List<Guild> guilds = new ArrayList<>(GuildWar2.get().dataStorage.guildStorage.values());
                if (event.getCurrentItem() == null)
                    return;
                for (int i = 0; i < ints.length; i++) {
                    if (ints[i] == event.getSlot()) {
                        int page = pageStorage.get(player);
                        Guild guild = guilds.get((21 * (page - 1)) + i);
                        if (guild.members.contains(player.getUniqueId().toString())) {
                            player.sendMessage(applyColor("&c Bạn Đã Ở Trong Bang Hội Này Ròi!"));
                            return;
                        }
                        if (pPlayer.guild != null) {
                            player.sendMessage(applyColor("&cBạn Đã Ở Trong Một Bang Hội"));
                            return;
                        }
                        if (guild.members.size() < GuildWar2.get().config.max_member) {
                            invite(player, pPlayer, guild, applyColor("&aBạn Vừa Xin Vào Bang Hội&f " + guild.name));
                        } else {
                            player.closeInventory();
                            player.sendMessage(applyColor("&cBang Hội Này Đã Đầy!"));
                            return;
                        }
                    }
                }
                if (event.getSlot() == 18) {
                    if (pageStorage.containsKey(player) && pageStorage.get(player) > 1) {
                        int oldPage = pageStorage.get(player);
                        pageStorage.replace(player,oldPage,oldPage-1);
                        open(player);
                    }
                } else if (event.getSlot() == 26) {
                    if (pageStorage.containsKey(player) && pageStorage.get(player) < (guilds.size() / 21) + 1) {
                        int oldPage = pageStorage.get(player);
                        pageStorage.replace(player,oldPage,oldPage+1);
                        open(player);
                    }
                }
            }
        }
    }

    static void invite(Player player, PPlayer pPlayer, Guild guild, String s) {
        if (pPlayer.wait != null) {
            player.sendMessage(applyColor("&cBạn Đã Xin Vào Một Bang Hội Khác!, Vui Lòng Đợi Đơn Cũ Hết Hiệu Lực!"));
            return;
        }
        player.sendMessage(s);
        pPlayer.wait = guild.name;
        for (String str : GuildWar2.get().config.guild_message) {
            if (str.contains("{/") && str.contains("/}")) {
                for (String uid : guild.members) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uid));
                    if (!op.isOnline())
                        continue;
                    String a = str.split("/}")[0].split("\\{/")[1];
                    String b = str.split("\\{/")[0];
                    String c = str.split("/}")[1];
                    ((Player)op).spigot().sendMessage(new TextComponentBuilder(b).setUnderline(false).build(),
                            new TextComponentBuilder(a).setUnderline(false).setDescription(applyColor("&eClick Vào Đây")).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/bh accept " + player.getUniqueId().toString())).build(),
                            new TextComponentBuilder(c).build());
                }
                continue;
            }
            guild.sendMessage(str.replace("{name}",player.getName()));
        }
        Cooldown.addPlayer(Bukkit.getOfflinePlayer(UUID.fromString(pPlayer.uuid)),()-> pPlayer.wait = null);
    }
}
