package org.thanhmagics.guildwar2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SignGUIBuilder {

    private final List<String> lines = new ArrayList<>();

    public SignGUIBuilder() {
        lines.add("");
        lines.add("");
        lines.add("");
        lines.add("");
    }

    public SignGUIBuilder setLine(int i, String content) {
        if (i > 3) {
            return this;
        }
        if (content != null) {
            this.lines.set(i, content);
            return this;
        }
        this.lines.set(i, "");
        return this;
    }

    public abstract void onClose(Player player, List<String> lines);

    public SignGUIBuilder open(Player player) {
        Bukkit.getScheduler().runTask(GuildWar2.get(), () -> {
            player.closeInventory();
            Location signLoc = player.getEyeLocation();
            signLoc = signLoc.clone().add(signLoc.clone().getDirection().multiply(-5));
            final Material material = player.getWorld().getBlockAt(signLoc).getType();
            final BlockPosition position = new BlockPosition(signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ());
            final TileEntitySign tileEntity = new TileEntitySign(position, null);
            SignText signText = tileEntity.a(true);
            for (int i = 0; i < lines.size(); i++) {
                signText = signText.a(i,IChatBaseComponent.a(ChatColor.translateAlternateColorCodes('&',lines.get(i))));
            }
            tileEntity.a(signText,true);
            final NetworkManager networkManager;
            try {
                Field field = PlayerConnection.class.getDeclaredField("h");
                field.setAccessible(true);
                networkManager = (NetworkManager) field.get(((CraftPlayer)player).getHandle().c);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            player.sendBlockChange(signLoc, Material.OAK_SIGN.createBlockData());
            ((CraftPlayer) player).getHandle().c.a(tileEntity.j());
            ((CraftPlayer) player).getHandle().c.a(new PacketPlayOutOpenSignEditor(position,true));

            final ChannelPipeline pipeline = networkManager.m.pipeline();
            if (pipeline.names().contains("SignGUI")) {
                pipeline.remove("SignGUI");
            }
            Location finalSignLoc = signLoc;
            Location finalSignLoc1 = signLoc;
            pipeline.addAfter("decoder", "SignGUI", new MessageToMessageDecoder<Packet<?>>() {
                @Override
                protected void decode(ChannelHandlerContext chc, Packet<?> packet, List<Object> out) {
                    try {
                        if (packet instanceof PacketPlayInUpdateSign) {
                            if (((PacketPlayInUpdateSign) packet).a().equals(position)) {
                                pipeline.remove("SignGUI");
                                player.sendBlockChange(finalSignLoc, finalSignLoc1.getBlock().getBlockData());
                                finalSignLoc.getBlock().setType(material);
                                List<String> list = new ArrayList<>(Arrays.asList(((PacketPlayInUpdateSign) packet).d()));
                                ((PacketPlayInUpdateSign) packet).d();
                                onClose(player, list);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    out.add(packet);
                }
            });
        });
        return this;
    }
}