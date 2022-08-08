package me.metallicgoat.hotbarmanageraddon.events;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.hotbarmanageraddon.Console;
import me.metallicgoat.hotbarmanageraddon.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class RoundStart implements Listener {

    @EventHandler
    public void onRoundStart(RoundStartEvent event){

        event.setGivingItems(false);

        final Arena arena = event.getArena();

        for(Player player : arena.getPlayers())
            manageSpawn(arena, arena.getPlayerTeam(player), player, true);

    }

    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent event){

        event.setGivingItems(false);

        final Arena arena = event.getArena();

        for(Player player : arena.getPlayers())
            manageSpawn(arena, arena.getPlayerTeam(player), player, false);

    }

    private static void manageSpawn(Arena arena, Team team, Player player, boolean firstSpawn){

        if(team == null)
            Console.printWarn("Error giving items on spawn. Please report this to MetallicGoat immediately");

        final Collection<ItemStack> itemsGiving = arena.getItemsGivenOnSpawn(player, team, firstSpawn, true);

        for(ItemStack itemStack : itemsGiving) {

            final ShopPage page = getItemPage(itemStack, player, arena, team);

            if(Util.isArmor(itemStack.getType())) {
                Helper.get().setPlayerArmor(player, itemStack, firstSpawn);
                continue;
            }

            if(page == null) {
                Helper.get().givePlayerItem(player, itemStack);
                continue;
            }

            ShopBuy.giveItemsProperly(itemStack, player, page, null, true);
        }
    }

    // Wrote this at 12 AM after an 8 hour workday on a weekend. Give me a break plz
    private static ShopPage getItemPage(ItemStack givenStack, Player player, Arena arena, Team team){

        for(ShopPage page : GameAPI.get().getShopPages()){
            for(ShopItem item : page.getItems()){
                for(ShopProduct product : item.getProducts()){
                    for(ItemStack checkedStack : product.getGivingItems(player, team, arena, 1)){
                        if(checkedStack.isSimilar(givenStack)){
                            return page;
                        }
                    }
                }
            }
        }
        return null;
    }
}