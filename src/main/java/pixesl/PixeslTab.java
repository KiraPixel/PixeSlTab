package pixesl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class PixeslTab extends JavaPlugin implements Listener {

    public void ChangeColor(Player player) {

        String chatWorldColor = getConfig().getString("chatWorldColor");
        String TABWorldColor = getConfig().getString("TABWorldColor");
        if (TABWorldColor == "false") {
            if (chatWorldColor == "false") {
                return;
            }
        }

        String world = player.getWorld().getName();
        ChatColor color;
        String path = String.join(".", "worldColor", world);
        String worldColor = getConfig().getString(path);

        if (worldColor == null){
            worldColor = "WHITE";
        }
        try{
            color = ChatColor.valueOf(worldColor);
        } catch (IllegalArgumentException e) {
            getServer().getLogger().info("[PixeslTAB]: "+ ChatColor.RED + "The wrong color is set for the " + world);
            worldColor = "WHITE";
        }
        color = ChatColor.valueOf(worldColor);
        if (!color.isColor()){
            color = ChatColor.WHITE;
        }
        if (TABWorldColor == "true") {
            player.setPlayerListName(color + player.getName());
        }
        if (chatWorldColor == "true") {
            player.setDisplayName(color + player.getName());
        }
    }


    public double PTPS() {
        double[] lasttps = getServer().getTPS();
        double tps = Math.round(lasttps[0] * 100.0D) / 100.0D;
        return tps;
    }

    public void CreateList(Player player){
        String enable = getConfig().getString("enableTabText");
        if (enable == "false"){
            return;
        }
        String headertext = getConfig().getString("header");
        String footertext = getConfig().getString("footer");
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(this,() -> {
            double tps = PTPS();
            int ping = player.getPing();
            String footer = footertext;
            footer = footer.replace("%tps%", ("§f" + tps));
            footer = footer.replace("%ping%", ("§f" + ping));
            player.setPlayerListHeaderFooter(headertext,footer);
        },5,5);

    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            CreateList(onlinePlayers);
        }
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public  void PTJoinEvent(PlayerJoinEvent event) {
        CreateList(event.getPlayer());
        ChangeColor(event.getPlayer());
    }


    @EventHandler
    public  void PTchangeWorld(PlayerChangedWorldEvent event) {
        ChangeColor(event.getPlayer());
    }

}
