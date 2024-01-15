package me.metallicgoat.hotbarmanageraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.tools.Pair;
import de.marcely.bedwars.tools.gui.*;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HotBarManagementSession {

  private final Player player;
  private final boolean inShop;
  private int selectedSlot = 0;
  private HashMap<Integer, String> categorySlotMap = new HashMap<>();

  public HotBarManagementSession(Player player, boolean inShop) {
    this.player = player;
    this.inShop = inShop;

    loadHotBarData();
    openGUI();
  }

  private void openGUI() {
    buildHotBarManagerGUI().open(player);
  }

  private GUI buildHotBarManagerGUI() {
    final ChestGUI gui = new ChestGUI(6, Message.build(ConfigValue.gui_title).done());

    addTopButtons(gui);

    // Categories
    final Collection<ShopPage> pages = GameAPI.get().getShopPages();

    int i = 0;

    for (ShopPage page : pages) {
      if (ConfigValue.excluded_categories.contains(page))
        continue;

      final String catName = ChatColor.stripColor(page.getDisplayName());
      final ItemStack guiIcon = Util.buildItemStack(
          page.getIcon().getType(),
          Message.build(ConfigValue.categories_gui_title).placeholder("category-name", catName).placeholder("selected-slot", String.valueOf(selectedSlot + 1)).done(),
          ConfigValue.categories_gui_lore, Arrays.asList(new Pair<>("category-name", catName), new Pair<>("selected-slot", String.valueOf(selectedSlot + 1))), 1);

      gui.setItem(new GUIItem(guiIcon, categoryClickListener(page.getName())), i, 2);
      i++;
    }

    gui.formatRow(2, CenterFormat.CENTRALIZED);

    Integer selectedSlotX = null;

    // Slots
    for (Map.Entry<Integer, String> entry : categorySlotMap.entrySet()) {
      if (entry.getKey() == null || entry.getValue() == null)
        continue;

      final ShopPage page = getPageByName(entry.getValue());

      if (page == null)
        continue;

      final String catName = ChatColor.stripColor(page.getDisplayName());
      final ItemStack guiIcon = Util.buildItemStack(
          page.getIcon().getType(),
          Message.build(ConfigValue.hotbar_gui_items_title).placeholder("category-name", catName).placeholder("selected-slot", String.valueOf(selectedSlot + 1)).done(),
          ConfigValue.hotbar_gui_items_lore, Arrays.asList(new Pair<>("category-name", catName), new Pair<>("selected-slot", String.valueOf(selectedSlot + 1))), 1);

      gui.setItem(new GUIItem(guiIcon, hotBarClickListener(entry.getKey())), entry.getKey(), 4);
    }

    i = 0;
    while (i < 9) {
      if (gui.getItem(i, 4) == null)
        gui.setItem(new GUIItem(new ItemStack(Material.AIR), hotBarClickListener(i)), i, 4);

      if (selectedSlot == i)
        selectedSlotX = i;

      i++;
    }


    // Top Divider
    drawDivider(gui, selectedSlotX, 3);

    // Bottom Divider
    drawDivider(gui, selectedSlotX, 5);

    return gui;
  }

  private void addTopButtons(ChestGUI gui) {
    final ItemStack closeItem = Util.buildItemStack(
        ConfigValue.close_button_icon,
        ConfigValue.close_button_title,
        ConfigValue.close_button_lore,
        null, 1);

    gui.setItem(new GUIItem(closeItem, new ClickListener() {
      @Override
      public void onClick(Player player, boolean b, boolean b1) {
        if (inShop) {
          GameAPI.get().openShop(player);
          return;
        }

        player.closeInventory();
      }
    }), 3, 0);

    final ItemStack resetItem = Util.buildItemStack(
        ConfigValue.reset_defaults_button_icon,
        ConfigValue.reset_defaults_button_title,
        ConfigValue.reset_defaults_button_lore,
        null, 1);

    gui.setItem(new GUIItem(resetItem, new ClickListener() {
      @Override
      public void onClick(Player player, boolean b, boolean b1) {
        final HashMap<Integer, String> newCategorySlotMap = new HashMap<>();

        for (Map.Entry<Integer, ShopPage> entry : ConfigValue.hotbar_defaults.entrySet())
          newCategorySlotMap.put(entry.getKey(), entry.getValue().getName());

        categorySlotMap = newCategorySlotMap;
        openGUI();
      }
    }), 5, 0);

    gui.addCloseListener(player -> saveHotBarData());
  }

  private void drawDivider(ClickableGUI gui, Integer selectedSlotX, int y) {
    int i = 0;
    while (i < 9) {

      if (selectedSlotX != null && selectedSlotX == i) {
        gui.setItem(ConfigValue.selected_slot_material, i, y, hotBarClickListener(i));
        i++;
        continue;
      }

      gui.setItem(ConfigValue.divider_material, i, y, hotBarClickListener(i));
      i++;
    }
  }

  private ShopPage getPageByName(String shopPage) {
    for (ShopPage page : GameAPI.get().getShopPages()) {
      if (shopPage.equals(page.getName()))
        return page;
    }

    return null;
  }

  private ClickListener categoryClickListener(String pageName) {
    return new ClickListener() {
      @Override
      public void onClick(Player player, boolean leftClick, boolean shiftClick) {

        if (!hasBeenModified(selectedSlot, pageName))
          return;

        categorySlotMap.put(selectedSlot, pageName);
        openGUI();
      }
    };
  }

  private ClickListener hotBarClickListener(int slot) {
    return new ClickListener() {
      @Override
      public void onClick(Player player, boolean leftClick, boolean shiftClick) {

        if (shiftClick) {
          categorySlotMap.remove(slot);
          openGUI();
          return;
        }

        if (selectedSlot != slot) {
          selectedSlot = slot;
          openGUI();
        }
      }
    };
  }

  private boolean hasBeenModified(int selectedSlot, String pageName) {
    for (Map.Entry<Integer, String> entry : categorySlotMap.entrySet()) {
      if (entry.getKey() == selectedSlot && entry.getValue().equals(pageName)) {
        return false;
      }
    }

    return true;
  }

  private void loadHotBarData() {
    PlayerDataAPI.get().getProperties(player.getUniqueId(), playerProperties -> {
      final Optional<String> json = playerProperties.get("hotbar_manager");

      if (!json.isPresent()) {
        openGUI();
        return;
      }

      final Gson gson = new Gson();
      categorySlotMap = gson.fromJson(json.get(), new TypeToken<HashMap<Integer, String>>() {
      }.getType());
    });
  }

  private void saveHotBarData() {
    final Gson gson = new Gson();
    final String json = gson.toJson(categorySlotMap);

    PlayerDataAPI.get().getProperties(player.getUniqueId(), playerProperties -> playerProperties.set("hotbar_manager", json));
  }
}
