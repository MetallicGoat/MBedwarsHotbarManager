package me.metallicgoat.hotbarmanageraddon.events;

import de.marcely.bedwars.api.event.ShopGUIPostProcessEvent;
import de.marcely.bedwars.api.game.shop.layout.ShopLayoutType;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.gui.ClickListener;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import me.metallicgoat.hotbarmanageraddon.HotBarManagementSession;
import me.metallicgoat.hotbarmanageraddon.Util;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AddShopButton implements Listener {

  @EventHandler
  public void onShopPostProcess(ShopGUIPostProcessEvent event) {
    if (event.getLayout().getType() != ShopLayoutType.HYPIXEL_V2)
      return;

    final ChestGUI gui = (ChestGUI) event.getGUI();

    if (gui == null)
      return;

    gui.setItem(new GUIItem(Util.buildItemStack(
        ConfigValue.open_gui_from_shop_material,
        Message.build(ConfigValue.open_gui_from_shop_title).done(),
        ConfigValue.open_gui_from_shop_lore,
        null, 1), new ClickListener() {

      @Override
      public void onClick(Player player, boolean b, boolean b1) {
        new HotBarManagementSession(player, true);
      }
    }), 53);
  }
}
