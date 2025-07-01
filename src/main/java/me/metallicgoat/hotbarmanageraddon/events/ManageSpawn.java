package me.metallicgoat.hotbarmanageraddon.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.hotbarmanageraddon.ArmorSet;
import me.metallicgoat.hotbarmanageraddon.HotbarManagerTools;
import me.metallicgoat.hotbarmanageraddon.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ManageSpawn implements Listener {

  private static void manageSpawn(Arena arena, Team team, Player player, boolean firstSpawn) {
    final Collection<ItemStack> itemsGiving = arena.getItemsGivenOnSpawn(player, team, firstSpawn, true);
    final ArmorSet armorSet = new ArmorSet();

    for (ItemStack itemStack : itemsGiving) {
      ShopPage page = HotbarManagerTools.getItemPage(itemStack, player, arena, team);

      // for give-items-on configs
      if (page == null)
        page = getRelatedShopPage(itemStack);

      if (Util.isArmor(itemStack.getType())) {
        armorSet.setArmor(itemStack);
        continue;
      }

      if (page == null) {
        Helper.get().givePlayerItem(player, itemStack);
        continue;
      }

      HotbarManagerTools.giveItemsProperly(itemStack, player, page, true);
    }

    armorSet.wearArmor(player);
  }

  // for give-items-on configs
  private static ShopPage getRelatedShopPage(ItemStack itemStack) {
    if (itemStack.getType().name().contains("SWORD"))
      return HotbarManagerTools.getSwordCategory();

    return null;
  }

  @EventHandler
  public void onRoundStart(RoundStartEvent event) {
    event.setGivingItems(false);

    final Arena arena = event.getArena();

    for (Player player : arena.getPlayers())
      manageSpawn(arena, arena.getPlayerTeam(player), player, true);
  }

  @EventHandler
  public void onRespawn(PlayerIngameRespawnEvent event) {
    event.setGivingItems(false);

    final Arena arena = event.getArena();
    final Player player = event.getPlayer();

    manageSpawn(arena, arena.getPlayerTeam(player), player, false);
  }
}
