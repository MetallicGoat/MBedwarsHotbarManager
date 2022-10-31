package me.metallicgoat.hotbarmanageraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerProperties;
import de.marcely.bedwars.tools.Helper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotbarManagerTools {

    public static boolean giveItemsProperly(ItemStack givenItem, Player player, ShopPage page, PlayerBuyInShopEvent event, boolean force) {

        if (page == null)
            return false;

        // We will handle giving the items
        if (event != null)
            event.setGivingProducts(false);

        final Integer slot = getPreferredSlot(givenItem.clone(), page, player, force);
        final Inventory inventory = player.getInventory();

        if (slot == null) {
            Helper.get().givePlayerItem(player, givenItem);
            return true;
        }

        final ItemStack currItemInSlot = inventory.getItem(slot);

        // Yay! The slot we want to use is already empty
        if (currItemInSlot == null)
            inventory.setItem(slot, givenItem);

        else {
            if (currItemInSlot.isSimilar(givenItem)) {

                final int total = currItemInSlot.getAmount() + givenItem.getAmount();
                final int maxStack = currItemInSlot.getMaxStackSize();

                // To many to fit in one slot
                if (total > maxStack) {
                    givenItem.setAmount(maxStack);
                    inventory.setItem(slot, givenItem);

                    // Keep redoing this until all items given
                    final ItemStack leftOver = givenItem.clone();
                    leftOver.setAmount(total - maxStack);

                    giveItemsProperly(leftOver, player, page, null, false);
                }

                // Yay! We don't need to find more space! Everything fits
                else {
                    givenItem.setAmount(total);
                    inventory.setItem(slot, givenItem);
                }

            } else {
                // Force move an item
                inventory.setItem(slot, givenItem);
                Helper.get().givePlayerItem(player, currItemInSlot.clone());
            }
        }

        return true;
    }

    // Wrote this at 12 AM after an 8 hour workday on a weekend. Give me a break plz
    public static ShopPage getItemPage(ItemStack givenStack, Player player, Arena arena, Team team) {

        for (ShopPage page : GameAPI.get().getShopPages()) {
            for (ShopItem item : page.getItems()) {
                for (ShopProduct product : item.getProducts()) {
                    for (ItemStack checkedStack : product.getGivingItems(player, team, arena, 1)) {
                        if (checkedStack.isSimilar(givenStack)) {
                            return page;
                        }
                    }
                }
            }
        }
        return null;
    }


    public static Integer getPreferredSlot(ItemStack givenItem, ShopPage page, Player player, boolean force) {

        final Optional<PlayerProperties> propertiesOptional = PlayerDataAPI.get().getPropertiesNow(player.getUniqueId());

        if (!propertiesOptional.isPresent())
            return null;

        final PlayerProperties properties = propertiesOptional.get();
        final Optional<String> json = properties.get("hotbar_manager");

        if (!json.isPresent())
            return null;

        final HashMap<Integer, String> layout = new Gson().fromJson(json.get(), new TypeToken<HashMap<Integer, String>>() {
        }.getType());

        if (layout == null)
            return null;

        final Inventory inventory = player.getInventory();
        final String category = page.getName();

        // Try to find similar slot
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {

            if (!entry.getValue().equals(category))
                continue;

            final ItemStack currStack = inventory.getItem(entry.getKey());

            if (currStack != null && currStack.isSimilar(givenItem) && currStack.getAmount() < currStack.getMaxStackSize())
                return entry.getKey();
        }

        // Try to find empty configured slot
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {

            if (!entry.getValue().equals(category))
                continue;

            final ItemStack currStack = inventory.getItem(entry.getKey());

            if (currStack == null)
                return entry.getKey();
        }

        // Check if we can force move any items
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {

            if (!entry.getValue().equals(category))
                continue;

            final ItemStack currStack = inventory.getItem(entry.getKey());

            if (!isItemInSameCategory(page, currStack, player))
                return entry.getKey();
        }

        return null;
    }

    public static boolean isItemInSameCategory(ShopPage page, ItemStack slotStack, Player player) {

        final Arena arena = GameAPI.get().getArenaByPlayer(player);

        if (arena == null)
            return false;

        for (ShopItem item : page.getItems()) {
            for (ShopProduct product : item.getProducts()) {
                for (ItemStack itemStack : product.getGivingItems(player, arena.getPlayerTeam(player), arena, 1)) {
                    if (itemStack.isSimilar(slotStack))
                        return true;
                }
            }
        }

        return false;
    }
}
