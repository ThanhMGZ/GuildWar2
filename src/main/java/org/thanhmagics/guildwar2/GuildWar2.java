package org.thanhmagics.guildwar2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class GuildWar2 extends JavaPlugin {

    private static GuildWar2 guildWar2;

    public CCConfig config;

    public DataStorage dataStorage;

    public ScheduledExecutorService scheduledExecutorService;

    public Calendar calendar;


    @Override
    public void onEnable() {
        // Plugin startup logic
        saveResource("config.yml",false);
        guildWar2 = this;
        getCommand("banghoi").setExecutor(new Command());
        getServer().getPluginManager().registerEvents(new Listeners(),this);
        getServer().getPluginManager().registerEvents(new ListGuildInv(),this);
        getServer().getPluginManager().registerEvents(new SettingInv(),this);
        getServer().getPluginManager().registerEvents(new SelectInv(),this);
        this.dataStorage = new DataStorage();
        File file = new File(getDataFolder(),"save.data");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                dataStorage = (DataStorage) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.config = new CCConfig(getConfig());
        resetTime();
        new Cooldown().run();
    }

    public void resetTime() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        String str = config.start;
        int h = Integer.parseInt(str.split(":")[0]);
        int m = Integer.parseInt(str.split(":")[1]);
        if (m < 10) {
            h-=1;
            m-= m + 10;
        } else {
            m-=10;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        this.calendar = calendar;
        scheduledExecutorService.scheduleAtFixedRate(War::countdown, calendar.getTimeInMillis() - System.currentTimeMillis(), TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    public ItemStack itemFromConfig(String path, FileConfiguration cf) {
        ItemStack rs = new ItemStack(Material.STONE);
        if (cf.contains(path + ".material"))
            rs = new ItemStack(Material.valueOf(cf.getString(path + ".material").toUpperCase()));
        if (cf.contains(path + ".skull")) {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(),null);
            gameProfile.getProperties().put("textures",new Property("textures",cf.getString(path + ".skull")));
            Field profileField;
            rs = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) rs.getItemMeta();
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            rs.setItemMeta(skullMeta);
        }
        ItemMeta meta = rs.getItemMeta();
        if (cf.contains(path + ".displayname"))
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(cf.getString(path + ".displayname"))));
        List<String> lore = new LinkedList<>();
        if (cf.contains(path + ".lore"))
            for (String value : cf.getStringList(path + ".lore"))
                lore.add(ChatColor.translateAlternateColorCodes('&',value));
        meta.setLore(lore);
        rs.setItemMeta(meta);
        return rs;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getDataFolder(),"save.data"));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dataStorage);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static GuildWar2 get() {
        return guildWar2;
    }
}
