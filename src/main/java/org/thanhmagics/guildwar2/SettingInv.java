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
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.thanhmagics.guildwar2.CCConfig.applyColor;

public class SettingInv implements Listener {

    static List<String> uuids = new ArrayList<>();

    public static void open(Player player) {
        UUID uuid = UUID.randomUUID();
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        if (pPlayer.guild == null) {
            player.sendMessage(applyColor("&cBạn Đang Không Ở Trong Bang Hội Nào Để Thực Hiện Lệnh!"));
            return;
        }
        if (!pPlayer.getGuild().leader.equals(player.getUniqueId().toString())) {
            player.sendMessage(applyColor("&cChỉ Đội Trưởng Mới Có Thể Sử Dụng Lệnh Này!"));
            return;
        }
        FileConfiguration config = GuildWar2.get().getConfig();
        Inventory inventory = Bukkit.createInventory(null,3*9,applyColor(config.getString("gui.setting.title")));

        inventory.setItem(10,GuildWar2.get().itemFromConfig("gui.setting.invite",config));
        inventory.setItem(11,GuildWar2.get().itemFromConfig("gui.setting.kick",config));
        inventory.setItem(12,GuildWar2.get().itemFromConfig("gui.setting.rename",config));
        inventory.setItem(13,GuildWar2.get().itemFromConfig("gui.setting.transfer",config));
        inventory.setItem(14,GuildWar2.get().itemFromConfig("gui.setting.disband",config));

        pPlayer.inventory = uuid.toString();
        uuids.add(uuid.toString());
        player.openInventory(inventory);
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
                if (event.getSlot() == 10) {
                    player.closeInventory();
                    new SignGUIBuilder() {
                        @Override
                        public void onClose(Player player, List<String> lines) {
                            try {
                                String name = lines.get(0);
                                if (name.length() == 0) {
                                    player.sendMessage(applyColor("&cNgười Chơi Không Tồn Tại!"));
                                    return;
                                }
                                if (pPlayer.getGuild().members.size() >= GuildWar2.get().config.max_member) {
                                    player.sendMessage(applyColor("&cBang Hội Đã Đầy!"));
                                    return;
                                }
                                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                                if (!op.isOnline()) {
                                    player.sendMessage(applyColor("&cNgười Chơi Này Hiện Không Trực Tuyến!"));
                                    return;
                                }
                                if (pPlayer.getGuild().members.contains(op.getUniqueId().toString())) {
                                    player.sendMessage(applyColor("&cNgười Chơi Này Đã Ở Trong Bang Hội!"));
                                    return;
                                }
                                Player p = (Player) op;
                                PPlayer p1 = GuildWar2.get().dataStorage.playerStorage.get(p.getUniqueId().toString());
                                if (p1.invited != null) {
                                    player.sendMessage(applyColor("&cNgười Chơi Này Đã Đực Mời Trước Đó!, Vui Lòng Đợi Đơn Cũ Hết Hiệu Lực!"));
                                    return;
                                }
                                for (String str : GuildWar2.get().config.message3) {
                                    if (str.contains("{/") && str.contains("/}")) {
                                        String a = str.split("/}")[0].split("\\{/")[1];
                                        String b = str.split("\\{/")[0];
                                        String c = str.split("/}")[1];
                                        p.spigot().sendMessage(new TextComponentBuilder(b).setUnderline(false).build(),
                                                new TextComponentBuilder(a).setUnderline(false).setDescription(applyColor("&eClick Vào Đây")).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh accept " + player.getUniqueId())).build(),
                                                new TextComponentBuilder(c).build());
                                    } else {
                                        p.sendMessage(str.replace("{name}", player.getName()));
                                    }
                                }
                                p1.invited = pPlayer.guild;
                                Cooldown.addPlayer(p, new Cooldown.Runnable() {
                                    @Override
                                    public void run(OfflinePlayer player) {
                                        PPlayer pPlayer1 = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(UUID.fromString(player.getUniqueId().toString())).getUniqueId().toString());
                                        pPlayer1.invited = null;
                                    }
                                });
                                player.sendMessage(applyColor("&aĐã Gửi Lời Mời Thành Công!"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.setLine(1,"▲▲▲▲▲").setLine(2,"Nhập Nội Dụng").open(player);
                } else if (event.getSlot() == 11) {
                    player.closeInventory();
                    if (pPlayer.getGuild().members.size() == 1) {
                        player.sendMessage(applyColor("&cBang Hội Đang Chỉ Có Một Mình Bạn"));
                        return;
                    }
                    SelectInv.open(player, new Runnable() {
                        @Override
                        public void run(String uid) {
                            PPlayer pp = GuildWar2.get().dataStorage.playerStorage.get(uid);
                            OfflinePlayer op =Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(uid)));
                            for (String s : GuildWar2.get().config.message5) {
                                pp.getGuild().sendMessage(s.replace("{player_name}",op.getName()));
                            }
                            pp.getGuild().members.remove(uid);
                            pp.guild = null;
                            if (!op.isOnline())
                                return;
                            for (String s : GuildWar2.get().config.message4)
                                ((Player)op).sendMessage(applyColor(s));
                        }
                    });
                } else if (event.getSlot() == 12) {
                    player.closeInventory();
                    if (War.instance != null) {
                        player.sendMessage(applyColor("&cKhông Thể Đổi Tên Bang Hội Lúc Này!"));
                        return;
                    }
                    new SignGUIBuilder() {
                        @Override
                        public void onClose(Player player, List<String> lines) {
                            String name = lines.get(0);
                            if (GuildWar2.get().dataStorage.guildStorage.containsKey(name)) {
                                player.sendMessage(applyColor("&cTên Này Đã Có Bang Hội Khác Sử Dụng!"));
                                return;
                            }
                            if (name.length() <= 1) {
                                player.sendMessage(applyColor("&cTên Này Quá Ngắn!"));
                                return;
                            }
                            Guild guild = pPlayer.getGuild();
                            Guild newGuild = guild.clone();
                            newGuild.name = name;
                            GuildWar2.get().dataStorage.guildStorage.remove(guild.name);
                            GuildWar2.get().dataStorage.guildStorage.put(name,newGuild);
                            for (String member : guild.members) {
                                PPlayer pPlayer1 = GuildWar2.get().dataStorage.playerStorage.get(member);
                                pPlayer1.guild = name;
                            }
                            player.sendMessage(applyColor("&aĐổi Tên Thành Công!"));
                        }
                    }.setLine(1,"▲▲▲▲▲").setLine(2,"Nhập Nội Dung").open(player);
                } else if (event.getSlot() == 13) {
                    player.closeInventory();
                    if (pPlayer.getGuild().members.size() == 1) {
                        player.sendMessage(applyColor("&cBang Hội Đang Chỉ Có Một Mình Bạn"));
                        return;
                    }
                    SelectInv.open(player, new Runnable() {
                        @Override
                        public void run(String uid) {
                            PPlayer pp = GuildWar2.get().dataStorage.playerStorage.get(uid);
                            for (String s : GuildWar2.get().config.message6)
                                pp.getGuild().sendMessage(s.replace("{player_name}",
                                        Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName()));
                            pp.getGuild().leader = uid;
                        }
                    });
                } else if (event.getSlot() == 14) {
                    if (War.instance != null) {
                        player.sendMessage(applyColor("&cBạn Không Thể Giải Tán Bang Hội Vào Lúc Này!"));
                        player.closeInventory();
                        return;
                    }
                    new SignGUIBuilder() {
                        @Override
                        public void onClose(Player player, List<String> lines) {
                            if (!lines.get(0).equals("123")) {
                                player.sendMessage(applyColor("&aXác Nhận Giải Tán Bang Hội Thất Bại!"));
                                return;
                            }
                            Guild guild = pPlayer.getGuild();
                            for (String s : GuildWar2.get().config.message9)
                                guild.sendMessage(s);
                            for (String uid : guild.members) {
                                PPlayer pPlayer1 = GuildWar2.get().dataStorage.playerStorage.get(uid);
                                pPlayer1.guild = null;
                            }
                            GuildWar2.get().dataStorage.guildStorage.remove(guild.name,guild);
                        }
                    }.setLine(1,"Nhập '123'").setLine(2,"Để Xác Nhận!").open(player);
                }
            }
        }
    }
}
