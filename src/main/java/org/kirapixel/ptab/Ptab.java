package org.kirapixel.ptab;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Ptab extends JavaPlugin implements Listener {



    public class pTabConfig {
        String chatWorldColor = getConfig().getString("chatWorldColor");
        String TABWorldColor = getConfig().getString("TABWorldColor");
        String enableTabText = getConfig().getString("enableTabText");
        String headertext = getConfig().getString("header");
        String footertext = getConfig().getString("footer");

        public ChatColor getWorldColor(String world){
            String path = String.join(".", "worldColor", world);
            String worldColor = getConfig().getString(path);
            ChatColor color;

            if (worldColor == null){
                worldColor = "WHITE";
            }
            try{
                ChatColor.valueOf(worldColor);
            } catch (IllegalArgumentException e) {
                getServer().getLogger().info("[PixeslTAB]: "+ ChatColor.RED + "The wrong color is set for the " + world);
                worldColor = "WHITE";
            }

            color = ChatColor.valueOf(worldColor);
            if (!color.isColor()){
                color = ChatColor.WHITE;
            }

            return(color);
        }
    }


    public void ChangeColor(Player player) {
        pTabConfig config = new pTabConfig();
        ChatColor color = config.getWorldColor(player.getWorld().getName());

        if (config.TABWorldColor == "false") {
            if (config.chatWorldColor == "false") {
                return;
            }
        }

        if (config.TABWorldColor == "true") {
            player.setPlayerListName(color + player.getName() + ChatColor.RESET);
        }

        if (config.chatWorldColor == "true") {
            player.setDisplayName(color + player.getName() + ChatColor.RESET);
        }
    }


    public double PTPS() {

        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            if (getConfig().getString("PaperSupport") == "true") {
                double[] lasttps = getServer().getTPS();
                double dtps = Math.round(lasttps[0] * 100.0D) / 100.0D;
                return (int)dtps;
            }
        } catch (ClassNotFoundException ignored) {}
        return 0;
    }


    public void CreateList(Player player){
        pTabConfig config = new pTabConfig();

        if (config.enableTabText == "false"){
            return;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(this,() -> {
            double tps = PTPS();
            int ping = player.getPing();
            String footer = config.footertext;
            footer = footer.replace("%tps%", ("§f" + tps));
            footer = footer.replace("%ping%", ("§f" + ping));
            player.setPlayerListHeaderFooter(config.headertext,footer);
        },5,5);

    }



    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            CreateList(onlinePlayers);

            new updaterCheck(this).getVersion(version -> {
                if (this.getDescription().getVersion().equals(version)) {
                    getLogger().info("[PixeSlTab] There is not a new update available.");
                } else {
                    getLogger().info("[PixeSlTab] There is a new update available.");
                }
            });
        }
    }


    @Override
    public void onDisable() {
    }


    @EventHandler
    public  void PTJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CreateList(player);
        ChangeColor(player);

        if (player.isOp()) {
            new updaterCheck(this).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    player.sendMessage(ChatColor.GOLD + "[PixeSlTab] There is a new update available! " + ChatColor.RESET + version);
                }
            });
        }
    }


    @EventHandler
    public  void PTchangeWorld(PlayerChangedWorldEvent event) {
        ChangeColor(event.getPlayer());
    }

}

