package org.thanhmagics.guildwar2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD
import java.util.List;
import java.util.Objects;
import java.util.UUID;
=======
import java.util.*;
>>>>>>> cde1a98 (Initial commit)

import static org.thanhmagics.guildwar2.CCConfig.applyColor;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        PPlayer pPlayer = GuildWar2.get().dataStorage.playerStorage.get(player.getUniqueId().toString());
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        String str = args[0];
        if (args.length == 1) {
            if (str.equalsIgnoreCase("list")) {
                ListGuildInv.open(player);
            } else if (str.equalsIgnoreCase("create")) {
                if (pPlayer.guild != null) {
                    player.sendMessage(applyColor("&cBạn Đang Ở Trong Một Bang Hội Khác"));
                    return true;
                }
                new SignGUIBuilder() {
                    @Override
                    public void onClose(Player player, List<String> lines) {
                        String name = lines.get(0);
                        if (name.length() == 0) {
                            player.sendMessage(applyColor("&cTên Quá Ngắn!"));
                            return;
                        }
                        if (GuildWar2.get().dataStorage.guildStorage.containsKey(name)) {
                            player.sendMessage(applyColor("&cTên Đã Tồn Tại"));
                            return;
                        }
                        if (name.equals("&cCHƯA CÓ AI")) {
                            player.sendMessage(applyColor("&cTên Không Hợp Lệ!"));
                            return;
                        }
                        Guild guild = new Guild();
                        guild.leader = player.getUniqueId().toString();
                        guild.name = name;
                        guild.members.add(player.getUniqueId().toString());
                        GuildWar2.get().dataStorage.guildStorage.put(name,guild);
                        pPlayer.guild = name;
                        pPlayer.wait = null;
                        pPlayer.invited = null;
                        player.sendMessage(applyColor("&aTạo Bang Hội Thành Công!"));
                    }
                }.setLine(1,"▲▲▲▲▲").setLine(2,"Nhập Tên Bang Hội").open(player);
            } else if (str.equalsIgnoreCase("war")) {
                String string = GuildWar2.get().config.war_mess3;
                long time = GuildWar2.get().calendar.getTimeInMillis() - System.currentTimeMillis();
                long second = time / 1000;
                long minute = second / 60;
                long hours = minute / 60;
                minute = minute % 60;
                player.sendMessage(applyColor(string.replace("{time}",hours + " giờ " + minute + " phút")));
            } else if (str.equalsIgnoreCase("chat")) {
                pPlayer.chat = !pPlayer.chat;
                if (pPlayer.chat)
                    player.sendMessage(applyColor("&aBật Chat Bang Hội Thành Công!"));
                else
                    player.sendMessage(applyColor("&aTắt Chat Bang Hội Thành Công!"));
            } else if (str.equalsIgnoreCase("startwar")) {
                if (player.hasPermission("op")) {
                    player.sendMessage(War.start() ? "Started" : "war already exists!");
                }
            }else if (str.equalsIgnoreCase("startwar2")) {
                if (player.hasPermission("op")) {
                    War.countdown();
                }
            }else if (str.equalsIgnoreCase("stopwar")) {
                if (player.hasPermission("op")) {
                    if (War.instance != null) {
                        War war = War.instance;
                        war.endwar = true;
                        war.end = true;
                        player.sendMessage("stop success!");
                    } else {
                        player.sendMessage("null");
                    }
                }
            }else if (str.equalsIgnoreCase("setting")) {
                if (pPlayer.guild != null) {
                    if (pPlayer.getGuild().leader.equals(player.getUniqueId().toString())) {
                        SettingInv.open(player);
                    } else {
                        player.sendMessage(applyColor("&cChỉ Đội Trưởng Mới Thực Hiện Được Lệnh Này!"));
                    }
                } else {
                    player.sendMessage(applyColor("&cBạn Không Có Tron Bang Hội Nào!"));
                }
<<<<<<< HEAD
=======
            } else if (str.equalsIgnoreCase("test")) {
                if (player.getName().equalsIgnoreCase("ongearzz") || player.getName().equalsIgnoreCase("thanhmagics")) {
                   // player.sendMessage(Arrays.toString(Cooldown.cd1.keySet().toArray()));
                }
>>>>>>> cde1a98 (Initial commit)
            } else if (str.equalsIgnoreCase("leave")) {
                if (pPlayer.guild != null) {
                    if (!pPlayer.getGuild().leader.equals(player.getUniqueId().toString())) {
                        Guild guild = pPlayer.getGuild();
                        guild.members.remove(player.getUniqueId().toString());
                        for (String string : GuildWar2.get().config.message8) {
<<<<<<< HEAD
                            guild.sendMessage(applyColor(string.replace("{player_name}",player.getName())));
=======
                            guild.sendMessage(applyColor(string.replace("{player_name}", player.getName())));
>>>>>>> cde1a98 (Initial commit)
                        }
                        for (String string : GuildWar2.get().config.message7) {
                            player.sendMessage(string);
                        }
                        pPlayer.guild = null;
                    } else {
                        player.sendMessage(applyColor("&cĐội Trưởng Không Thể Rời Khỏi Bang Hội!&7(Hãy Chuyển Quyền Đội Trưởng Hoặc Giải Tán Bang Hội)"));
                    }
                } else {
                    player.sendMessage(applyColor("&cBạn Không Có Tron Bang Hội Nào!"));
                }
            } else if (args[0].equals("test")) {
                if (player.getName().equalsIgnoreCase("ongearzz")) {
                    player.sendMessage(player.getWorld().getName());
                }
            }
        } else if (args.length >= 2) {
            if (str.equalsIgnoreCase("join")) {
                if (pPlayer.guild != null) {
                    player.sendMessage(applyColor("&cBạn Đã Ở Trong Một Bang Hội Khác!"));
                    return true;
                }
                String name = args[1];
                PPlayer pPlayer1 = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(name).getUniqueId().toString());
                if (pPlayer1 == null) {
                    player.sendMessage(applyColor("&cNgười Chơi Này Không Tồn Tại!"));
                    return true;
                }
                if (pPlayer1.guild == null) {
                    player.sendMessage(applyColor("&cNgười Chơi Này Không Có Trong Bang Hội Nào!"));
                    return true;
                }
                Guild guild = pPlayer1.getGuild();
                if (guild.members.size() < GuildWar2.get().config.max_member) {
                    ListGuildInv.invite(player, pPlayer, guild, applyColor("&aBạn Vừa Xin Vào Bang Hội&f " + guild.name));
                } else {
                    player.sendMessage(applyColor("&cBang Hội Của Người Chơi Này Đã Đầy!"));
                }
            } else if (str.equalsIgnoreCase("accept")) {
                String uid = args[1];
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uid));
                PPlayer pPlayer1 = GuildWar2.get().dataStorage.playerStorage.get(op.getUniqueId().toString());
                if (pPlayer1.wait != null) {
                    Guild guild = GuildWar2.get().dataStorage.guildStorage.get(pPlayer1.wait);
                    if (guild == null) {
                        player.sendMessage(applyColor("&cBang Hội Đã Giải Tán Hoặc Không Tồn Tại!"));
                        return true;
                    }
                    if (!guild.members.contains(player.getUniqueId().toString())) {
                        player.sendMessage(applyColor("&cĐơn Đã Hết Hiệu Lực!"));
                        return true;
                    }
                    guild.members.add(op.getUniqueId().toString());
                    for (String string : GuildWar2.get().config.message2)
                        guild.sendMessage(string.replace("{name}", Objects.requireNonNull(op.getName())));
                    pPlayer1.wait = null;
                    pPlayer1.invited = null;
                    pPlayer1.guild = guild.name;
                    Cooldown.removePlayer(op);
                } else if (pPlayer.invited != null) {
                    Guild guild = GuildWar2.get().dataStorage.guildStorage.get(pPlayer.invited);
                    if (guild == null) {
                        player.sendMessage(applyColor("&cBang Hội Đã Giải Tán Hoặc Không Tồn Tại!"));
                        return true;
                    }
                    guild.members.add(pPlayer.uuid);
                    for (String string : GuildWar2.get().config.message2) {
                        guild.sendMessage(string.replace("{name}",player.getName()));
                    }
<<<<<<< HEAD
                    pPlayer1.guild = guild.name;
                    pPlayer1.invited = null;
                    pPlayer1.wait = null;
=======
                    pPlayer.guild = guild.name;
                    pPlayer.invited = null;
                    pPlayer.wait = null;
>>>>>>> cde1a98 (Initial commit)
                    Cooldown.removePlayer(op);
                }
            } else if (str.equalsIgnoreCase("ifp")) {
                PPlayer p = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                player.sendMessage("guild: " + p.guild);
                player.sendMessage("Inv: " + p.invited);
                player.sendMessage("W: " + p.wait);
            } else if (str.equalsIgnoreCase("rsp")) {
                if (player.hasPermission("op")) {
                    PPlayer p = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                    p.wait = null;
                    p.invited = null;
                    p.guild = null;
                }
            } else if (str.equalsIgnoreCase("adm1")) {
                if (player.hasPermission("op")) {
                    PPlayer p = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                    Guild guild = p.getGuild();
                    if (guild == null) {
                        player.sendMessage("guild null");
                        return true;
                    }
                    GuildWar2.get().dataStorage.guildStorage.remove(guild.name);
                    for (String uid : guild.members) {
                        PPlayer pr = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(UUID.fromString(uid)).getUniqueId().toString());
                        pr.guild = null;
                    }
                }
            } else if (str.equalsIgnoreCase("adm2")) {
                if (player.hasPermission("op")) {
                    Guild guild = GuildWar2.get().dataStorage.guildStorage.get(args[1]);
                    if (guild == null) {
                        player.sendMessage("guild null!");
                        return true;
                    }
                    GuildWar2.get().dataStorage.guildStorage.remove(guild.name);
                    for (String uid : guild.members) {
                        PPlayer pr = GuildWar2.get().dataStorage.playerStorage.get(Bukkit.getOfflinePlayer(UUID.fromString(uid)).getUniqueId().toString());
                        pr.guild = null;
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("create")) {
                    Bukkit.dispatchCommand(player,"bh create");
                } else if (args[0].equalsIgnoreCase("list")) {
                    Bukkit.dispatchCommand(player,"bh list");
                } else if (args[0].equalsIgnoreCase("leave")) {
                    Bukkit.dispatchCommand(player,"bh leave");
                } else if (args[0].equalsIgnoreCase("setting")) {
                    Bukkit.dispatchCommand(player,"bh setting");
                } else if (args[0].equalsIgnoreCase("war")) {
                    Bukkit.dispatchCommand(player,"bh war");
                } else if (args[0].equalsIgnoreCase("chat")) {
                    Bukkit.dispatchCommand(player,"bh chat");
                } else {
                    sendHelp(player);
                }
            }
        } else {
            sendHelp(player);
        }
        return true;
    }

    String applyColor(String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    void sendHelp(Player player) {
        for (String s : GuildWar2.get().config.helpCommand) {
            player.sendMessage(s);
        }
    }
}