package me.metallicgoat.hotbarmanageraddon.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.hotbarmanageraddon.HotbarManagerTools;
import me.metallicgoat.hotbarmanageraddon.Util;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ManageBuy implements Listener {

    // TODO Simplify this mess

    /*
     * 1. Try to put item in slot with similar itemstack
     * 2. Try to put item in null slot for that category
     * 3. Try and force move an item
     * 4. Give up. Add Item to inventory normally.
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShopBuy(PlayerBuyInShopEvent event) {

        if (!event.getProblems().isEmpty())
            return;

        final ShopPage page = event.getItem().getPage();
        final Player player = event.getPlayer();

        if(ConfigValue.excluded_categories.contains(page))
            return;


        final Collection<ItemStack> givenItems = getGivingItems(event.getItem(), player, event.getArena());

        for(ItemStack givenItem : givenItems) {
            if(!Util.isArmor(givenItem.getType()))
                HotbarManagerTools.giveItemsProperly(givenItem, player, page, event, false);
        }
    }

    private Collection<ItemStack> getGivingItems(ShopItem item, Player player, Arena arena) {

        final Collection<ItemStack> itemsGiven = new ArrayList<>();

        for (ShopProduct product : item.getProducts()) {

            final Collection<ItemStack> items = Arrays.asList(product.getGivingItems(player, arena.getPlayerTeam(player), arena, 1));

            itemsGiven.addAll(items);
        }

        return itemsGiven;
    }
}
