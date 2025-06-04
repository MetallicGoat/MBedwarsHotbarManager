package me.metallicgoat.hotbarmanageraddon.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import me.metallicgoat.hotbarmanageraddon.HotbarManagerTools;
import me.metallicgoat.hotbarmanageraddon.Util;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ManageBuy implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST) // NOTE: This might be better as MONITOR
  public void onShopBuy(PlayerBuyInShopEvent event) {
    if (!event.getProblems().isEmpty())
      return;

    final ShopPage page = event.getItem().getPage();
    final Player player = event.getPlayer();

    if (ConfigValue.excluded_categories.contains(page))
      return;

    final Arena arena = event.getArena();
    final ShopItem item = event.getItem();

    // Implement giving products ourselves:
    if (event.isGivingProducts()) {
      event.setGivingProducts(false);

      final Team team = arena != null ? arena.getPlayerTeam(player) : null;
      final int multiplier = event.getMultiplier();

      for (ShopProduct product : item.getProducts()) {
        // Give using API
        if (!(product instanceof ItemShopProduct)) {
          product.give(player, team, arena, multiplier);
          continue;
        }

        // Our own implementation for items
        for (ItemStack givenItem : product.getGivingItems(player, team, arena, multiplier)) {
          if (!Util.isArmor(givenItem.getType())) {
            HotbarManagerTools.giveItemsProperly(givenItem, player, page, false);
          }
        }
      }
    }
  }
}
