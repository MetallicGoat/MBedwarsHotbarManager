package me.metallicgoat.hotbarmanageraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.player.PlayerProperties;
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

public class OnShopBuy implements Listener {

    // TODO Simplify this mess

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShopBuy(PlayerBuyInShopEvent event) {

        if (!event.getProblems().isEmpty())
            return;

        final ShopPage page = event.getItem().getPage();
        final Player player = event.getPlayer();

        // TODO check if category is enabled

        final ItemStack givenItem = getGivingItem(event.getItem(), player, event.getArena());
        final Integer slot = getPreferredSlot(givenItem.clone(), page, player);

        if (slot == null)
            return;

        final Inventory inventory = player.getInventory();


        event.setGivingProducts(false);

        if (inventory.getItem(slot) == null)
            inventory.addItem(givenItem);

        else {

            final ItemStack is = inventory.getItem(slot).clone();

            // TODO make better
            if (is.isSimilar(givenItem)) {
                final int total = is.getAmount() + givenItem.getAmount();
                final int maxStack = is.getMaxStackSize();

                if(total > maxStack){
                    givenItem.setAmount(maxStack);
                    inventory.setItem(slot, givenItem);
                    addExtra(givenItem, total - maxStack, inventory);
                }else{
                    givenItem.setAmount(total);
                    inventory.setItem(slot, givenItem);
                }

            } else {
                inventory.setItem(slot, givenItem);
                addItem(is, inventory);
            }
        }
    }

    public void addExtra(ItemStack given, int amount, Inventory inventory){

        final int maxStack = given.getMaxStackSize();

        while(amount > 0){

            final int giveAmount = Math.min(amount, maxStack);
            final ItemStack givenStack = given.clone();

            givenStack.setAmount(giveAmount);
            addItem(givenStack, inventory);

            amount = amount - giveAmount;
        }
    }

    public void addItem(ItemStack itemStack, Inventory inventory){

        int slot = 0;
        int amount = itemStack.getAmount();

        while(slot < 36){
            final ItemStack currentStack = inventory.getItem(slot);

            if(currentStack != null && currentStack.isSimilar(itemStack)){

                final int currAmount = currentStack.getAmount();
                final int maxStack = currentStack.getMaxStackSize();
                final ItemStack given = itemStack.clone();
                given.setAmount(Math.min(maxStack, amount + currAmount));
                amount = amount - (maxStack - currAmount);

                inventory.setItem(slot, given);

                if(amount < 1)
                    return;

            }

            slot++;
        }

        slot = 9;

        final ItemStack given = itemStack.clone();
        given.setAmount(amount);

        while(slot < 36){
            final ItemStack currentStack = inventory.getItem(slot);

            if(currentStack == null) {
                inventory.setItem(slot, given);
                return;
            }

            slot++;
        }

        // TODO Possible items will be lost after inventory full
        inventory.addItem(given);
    }

    private static ItemStack getGivingItem(ShopItem item, Player player, Arena arena) {

        for (ShopProduct product : item.getProducts()) {

            final ItemStack[] items = product.getGivingItems(player, arena.getPlayerTeam(player), arena, 1);

            if (items != null && items.length != 0)
                return items[0];
        }

        return new ItemStack(Material.AIR);
    }

    private static Integer getPreferredSlot(ItemStack givenItem, ShopPage page, Player player) {

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
        Integer backupSlot = null;

        for (Map.Entry<Integer, String> entry : layout.entrySet()) {

            if (!entry.getValue().equals(category))
                continue;

            final ItemStack currStack = inventory.getItem(entry.getKey());

            if (currStack == null || (currStack.isSimilar(givenItem) && currStack.getAmount() < currStack.getMaxStackSize()))
                return entry.getKey();

            if (backupSlot == null)
                backupSlot = entry.getKey();
        }

        // TODO offer empty hotbar slots

        return backupSlot;
    }
}
