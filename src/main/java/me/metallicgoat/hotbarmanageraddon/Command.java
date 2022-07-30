package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.api.command.CommandHandler;
import de.marcely.bedwars.api.command.SubCommand;
import de.marcely.bedwars.libraries.org.jetbrains.annotations.Nullable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Command implements CommandHandler {

    private SubCommand cmd;

    @Override
    public Plugin getPlugin() {
        return HotbarManagerPlugin.getInstance();
    }

    @Override
    public void onRegister(SubCommand subCommand) {
        this.cmd = subCommand;
    }

    @Override
    public void onFire(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return;

        new HotBarManagementSession((Player) commandSender, false);

    }

    @Override
    public @Nullable List<String> onAutocomplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
