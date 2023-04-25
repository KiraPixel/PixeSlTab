package org.kirapixel.ptab;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PTABPlaceholderAPI extends PlaceholderExpansion {

    private Plugin plugin;

    public PTABPlaceholderAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "pixesltab";
    }

    @Override
    public String getAuthor() {
        return "KiraPixel";
    }

    @Override
    public String getVersion() {
        return (plugin.getDescription().getVersion());
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equalsIgnoreCase("ptab_worldcolor")) {
            return ("testcolor");
        }

        if (identifier.equalsIgnoreCase("ptab_playerworldcolor")) {
            return ("playercolor");
        }

        return null;
    }
}
