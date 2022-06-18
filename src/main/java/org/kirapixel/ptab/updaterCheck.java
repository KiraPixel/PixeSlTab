package org.kirapixel.ptab;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class updaterCheck {

    private final JavaPlugin plugin;
    private final int resourceId;

    public updaterCheck(JavaPlugin plugin) {
        this.plugin = plugin;
        this.resourceId = 98291;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("[PixeSlTab] Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
