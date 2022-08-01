package me.metallicgoat.hotbarmanageraddon.events;

import com.google.gson.Gson;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerJoin implements Listener {

    // not on gui open because they may into a game first

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayerDataAPI.get().getProperties(event.getPlayer().getUniqueId(), playerProperties -> {

            if(playerProperties.get("hotbar_manager").isPresent())
                return;

            final HashMap<Integer, String> categorySlotMap = new HashMap<>();

            for(Map.Entry<Integer, ShopPage> entry : ConfigValue.hotbar_defaults.entrySet())
                categorySlotMap.put(entry.getKey(), entry.getValue().getName());

            playerProperties.set("hotbar_manager", new Gson().toJson(categorySlotMap));

        });
    }
}
