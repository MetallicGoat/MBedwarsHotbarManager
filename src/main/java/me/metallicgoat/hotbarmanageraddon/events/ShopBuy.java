package me.metallicgoat.hotbarmanageraddon.events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerProperties;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopBuy implements Listener {

    // TODO Simplify this mess
    // TODO add support for shopItems with multiple products


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

        // TODO make sure item is not wearable

        final ItemStack givenItem = getGivingItem(event.getItem(), player, event.getArena());

        giveItemsProperly(givenItem, player, page, event, false);


    }

    public static void giveItemsProperly(ItemStack givenItem, Player player, ShopPage page, PlayerBuyInShopEvent event, boolean force){

        // We will handle giving the items
        if(event != null)
            event.setGivingProducts(false);

        final Integer slot = getPreferredSlot(givenItem.clone(), page, player, force);
        final Inventory inventory = player.getInventory();

        if (slot == null) {
            Helper.get().givePlayerItem(player, givenItem);
            return;
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
                Helper.get().givePlayerItem(player, givenItem);
            }
        }
    }

    private ItemStack getGivingItem(ShopItem item, Player player, Arena arena) {

        for (ShopProduct product : item.getProducts()) {

            final ItemStack[] items = product.getGivingItems(player, arena.getPlayerTeam(player), arena, 1);

            if (items != null && items.length != 0)
                return items[0];
        }

        return new ItemStack(Material.AIR);
    }

    private static Integer getPreferredSlot(ItemStack givenItem, ShopPage page, Player player, boolean force) {

        final Optional<PlayerProperties> propertiesOptional = PlayerDataAPI.get().getPropertiesNow(player.getUniqueId());

        if (!propertiesOptional.isPresent())
            return null;

        final PlayerProperties properties = propertiesOptional.get();
        final Optional<String> json = properties.get("hotbar_manager");

        if (!json.isPresent())
            return null;

        final HashMap<Integer, String> layout = new Gson().fromJson(json.get(), new TypeToken<HashMap<Integer, String>>() {}.getType());

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

        // Try to find empty slot
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {

            if (!entry.getValue().equals(category))
                continue;

            final ItemStack currStack = inventory.getItem(entry.getKey());

            if (currStack == null)
                return entry.getKey();
        }

        // Check if we can force move any items
        if(isHotbarFull(inventory) || force) {

            for (Map.Entry<Integer, String> entry : layout.entrySet()) {

                if (!entry.getValue().equals(category))
                    continue;

                final ItemStack currStack = inventory.getItem(entry.getKey());

                if (!isItemInSameCategory(page, currStack, player))
                    return entry.getKey();
            }
        }

        return null;
    }

    private static boolean isItemInSameCategory(ShopPage page, ItemStack slotStack, Player player){

        final Arena arena = GameAPI.get().getArenaByPlayer(player);

        if(arena == null)
            return false;

        for(ShopItem item : page.getItems()){
            for(ShopProduct product : item.getProducts()){
                for(ItemStack itemStack : product.getGivingItems(player, arena.getPlayerTeam(player), arena, 1)){
                    if(itemStack.isSimilar(slotStack))
                        return true;
                }
            }
        }

        return false;
    }

    public static boolean isHotbarFull(Inventory inventory){

        int i = 0;

        while(i < 9){
            if(inventory.getItem(i) == null)
                return false;

            i++;
        }
        return true;
    }
}
