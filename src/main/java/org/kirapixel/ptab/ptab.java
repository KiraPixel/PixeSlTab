package org.kirapixel.ptab;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class ptab extends JavaPlugin implements Listener {


    public boolean isPaper(){
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }


    public class pTabConfig {
        String pluginPrefix = ("[PixeSlTab] ");
        String pluginPrefixChat = (ChatColor.WHITE+"["+ChatColor.GOLD+"PixeSlTab"+ChatColor.WHITE+"] "+ChatColor.RESET);
        String chatWorldColor = getConfig().getString("chatWorldColor");
        String TABWorldColor = getConfig().getString("TABWorldColor");
        String enableTabText = getConfig().getString("enableTabText");
        String headertext = getConfig().getString("header");
        String footertext = getConfig().getString("footer");
        String checkUpdate = getConfig().getString("check-update");

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
                getServer().getLogger().info(pluginPrefix + " "+ ChatColor.RED + "The wrong color is set for the " + world);
                worldColor = "WHITE";
            }

            color = ChatColor.valueOf(worldColor);
            if (!color.isColor()){
                color = ChatColor.WHITE;
            }

            return(color);
        }
    }


    @SuppressWarnings("deprecation")
    public void ChangeColor(Player player) {
        pTabConfig config = new pTabConfig();
        ChatColor color = config.getWorldColor(player.getWorld().getName());

        if (config.TABWorldColor.equals("false")) {
            if (config.chatWorldColor.equals("false")) {
                return;
            }
        }

        if (config.TABWorldColor.equals("true")) {
            player.setPlayerListName(color + player.getName() + ChatColor.RESET);
        }

        if (config.chatWorldColor.equals("true")) {
            player.setDisplayName(color + player.getName() + ChatColor.RESET);
        }
    }



    public double pTPS() {
         if (isPaper()){
             double[] lastTPS = getServer().getTPS();
             double tps = Math.round(lastTPS[0] * 100.0D) / 100.0D;
             return (int)tps;
        }
        return 0;
    }



    public void CreateList(Player player){
        pTabConfig config = new pTabConfig();
        if (config.enableTabText.equals("false")){
            return;
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(this,() -> {
            double tps = pTPS();
            int ping = player.getPing();
            String footer = config.footertext;
            footer = footer.replace("%tps%", ("§f" + tps));
            footer = footer.replace("%ping%", ("§f" + ping));
            //noinspection deprecation
            player.setPlayerListHeaderFooter(config.headertext,footer);
        },5,5);
    }


    @Override
    public void onEnable() {
        pTabConfig config = new pTabConfig();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            CreateList(onlinePlayers);


        new updaterCheck(this).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info( config.pluginPrefix + "There is not a new update available.");
            } else {
                getLogger().info(config.pluginPrefix + "here is a new update available.");
            }
        });
        }


        getCommand("ptab").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

                if(args.length == 0){
                    sender.sendMessage(config.pluginPrefixChat + ChatColor.GRAY +"/ptab reload.");
                    return true;
                }

                if(args[0].equalsIgnoreCase("reload")){
                    reloadConfig();
                    CreateList(getServer().getPlayer(sender.getName()));
                    sender.sendMessage(config.pluginPrefixChat + ChatColor.GRAY + "config reload. Please relogin to the server");
                    return true;
                }

                return true;
            }
        });
    }


    @Override
    public void onDisable() {
    }


    @EventHandler
    public  void JoinEvent(PlayerJoinEvent event) {
        pTabConfig config = new pTabConfig();
        Player player = event.getPlayer();
        CreateList(player);
        ChangeColor(player);

        if (player.isOp()) {
            if (config.checkUpdate.equals("true")){
                new updaterCheck(this).getVersion(version -> {
                    if (!this.getDescription().getVersion().equals(version)) {
                        player.sendMessage(config.pluginPrefixChat + "There is a new update available! " + ChatColor.RESET + version);
                    }
                });
            }
        }
    }


    @EventHandler
    public  void ChangeWorld(PlayerChangedWorldEvent event) {
        ChangeColor(event.getPlayer());
    }

}

