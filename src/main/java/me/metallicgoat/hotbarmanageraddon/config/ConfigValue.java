package me.metallicgoat.hotbarmanageraddon.config;

import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.tools.Helper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConfigValue {

    public static final String gui_title = "Hotbar Manager";

    public static String close_button_title = "Close";
    public static List<String> close_button_lore = Collections.singletonList("Exit Menu");
    public static Material close_button_icon = Helper.get().getMaterialByName("ARROW");

    public static String reset_defaults_button_title = "Reset";
    public static List<String> reset_defaults_button_lore = Collections.singletonList("Reset settings to defaults");
    public static Material reset_defaults_button_icon = Helper.get().getMaterialByName("BARRIER");

    // NOTE: defaults loaded on start / reload
    public static HashMap<Integer, ShopPage> hotbar_defaults = new HashMap<>();
    public static List<ShopPage> excluded_categories = new ArrayList<>();

    public static ItemStack divider_material = Helper.get().parseItemStack("gray_stained_glass_pane");
    public static ItemStack selected_slot_material = Helper.get().parseItemStack("red_stained_glass_pane");

    // TODO default Lore
    public static String categories_gui_title = "{category-display-name}";
    public static List<String> categories_gui_lore = new ArrayList<>();

    // TODO default lore
    public static String hotbar_gui_items_title = "{category-display-name}";
    public static List<String> hotbar_gui_items_lore = new ArrayList<>();

}
