package me.metallicgoat.hotbarmanageraddon.config;

import de.marcely.bedwars.Console;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.event.ConfigsLoadEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LoadConfigs implements Listener {

    @EventHandler
    public void onConfigLoad(ConfigsLoadEvent event) {
        if(!event.isStartup())
            loadConfigs();
    }

    public static void loadConfigs() {

        final long start = System.currentTimeMillis();

        // Load some defaults that need to be refreshed on mbedwars reload
        // Tries to guess what the sword and armor categories are (Assumes hypixel-like store structure... Kinda)
        {
            final ShopPage armorCategory = getArmorCategory();
            ConfigValue.excluded_categories = armorCategory != null ? Collections.singletonList(armorCategory) : new ArrayList<>();

            final ShopPage swordCategory = getSwordCategory();
            ConfigValue.hotbar_defaults = swordCategory != null ? new HashMap<Integer, ShopPage>() {{ put(0, swordCategory); }} : new HashMap<>();

        }

        MainConfig.load();

        final long end = System.currentTimeMillis();

        Console.printInfo("Configs loaded in " + (end - start) + "ms.");
    }

    public static ShopPage getArmorCategory(){

        for(ShopPage page : GameAPI.get().getShopPages()){
            for(ShopItem item : page.getItems()){
                final String name = item.getIcon().getType().name();

                if(name.contains("LEGGINGS") || name.contains("BOOTS") || name.contains("CHESTPLATE") || name.contains("HELMET"))
                    return page;
            }
        }

        return null;
    }

    public static ShopPage getSwordCategory(){

        for(ShopPage page : GameAPI.get().getShopPages()){
            for(ShopItem item : page.getItems()){
                if(item.getIcon().getType().name().contains("SWORD"))
                    return page;
            }
        }

        return null;
    }
}
