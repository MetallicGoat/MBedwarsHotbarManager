package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.BedwarsAddon;
import de.marcely.bedwars.api.command.SubCommand;
import me.metallicgoat.hotbarmanageraddon.config.LoadConfigs;
import me.metallicgoat.hotbarmanageraddon.events.PlayerJoin;
import me.metallicgoat.hotbarmanageraddon.events.ShopBuy;
import org.bukkit.plugin.PluginManager;

public class HotbarManagerAddon extends BedwarsAddon {

    private final HotbarManagerPlugin plugin;
    private SubCommand command;

    public HotbarManagerAddon(HotbarManagerPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
        this.command = null;
    }

    @Override
    public String getName(){
        return "MBedwarsHotbarManager";
    }

    public static void registerEvents(){

        final HotbarManagerPlugin plugin = HotbarManagerPlugin.getInstance();
        final PluginManager manager = plugin.getServer().getPluginManager();

        manager.registerEvents(new LoadConfigs(), plugin);

        manager.registerEvents(new PlayerJoin(), plugin);
        manager.registerEvents(new ShopBuy(), plugin);

    }

    void registerCommands(){
        final SubCommand cmd = BedwarsAPI.getRootCommandsCollection().addCommand("hotbarmanager");

        if(cmd == null)
            return;

        cmd.setOnlyForPlayers(true);
        cmd.setUsage("");
        cmd.setHandler(new Command());
        cmd.setAliases("hbm", "hotbar", "edithotbar");

        this.command = cmd;

    }

    void unregisterCommands(){
        if(command != null)
            BedwarsAPI.getRootCommandsCollection().removeCommand(command);
    }
}
