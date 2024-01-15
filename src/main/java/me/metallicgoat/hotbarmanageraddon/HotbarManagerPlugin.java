package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.api.BedwarsAPI;
import lombok.Getter;
import me.metallicgoat.hotbarmanageraddon.config.LoadConfigs;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HotbarManagerPlugin extends JavaPlugin {

  public static final int MIN_MBEDWARS_API_VER = 15;
  public static final String MIN_MBEDWARS_VER_NAME = "5.0.15";

  @Getter
  static private HotbarManagerPlugin instance;
  @Getter
  static private HotbarManagerAddon addon;

  public void onEnable() {

    instance = this;

    if (!checkMBedwars()) return;
    if (!registerAddon()) return;

    addon.registerCommands();

    // TODO new Metrics(this, 11928);

    HotbarManagerAddon.registerEvents();

    PluginDescriptionFile pdf = this.getDescription();

    Console.printInfo(
        "------------------------------",
        pdf.getName() + " For MBedwars",
        "By: " + pdf.getAuthors(),
        "Version: " + pdf.getVersion(),
        "------------------------------"
    );

    BedwarsAPI.onReady(LoadConfigs::loadConfigs);
  }

  public void onDisable() {
    if (addon != null)
      addon.unregisterCommands();
  }

  private boolean checkMBedwars() {
    try {
      final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
      final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

      if (apiVersion < MIN_MBEDWARS_API_VER)
        throw new IllegalStateException();
    } catch (Exception e) {
      getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MIN_MBEDWARS_VER_NAME);
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  private boolean registerAddon() {
    addon = new HotbarManagerAddon(this);

    if (!addon.register()) {
      getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }
}
