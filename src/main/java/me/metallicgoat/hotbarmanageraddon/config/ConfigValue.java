package me.metallicgoat.hotbarmanageraddon.config;

import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.tools.Helper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ConfigValue {

    public static final String gui_title = "&8Hotbar Manager";

    public static String close_button_title = "&cClose Menu";
    public static List<String> close_button_lore = Arrays.asList(
            "&7After closing you should notice",
            "&7changes taking effect immediately"
    );
    public static Material close_button_icon = Helper.get().getMaterialByName("ARROW");

    public static String reset_defaults_button_title = "&cReset to default";
    public static List<String> reset_defaults_button_lore = Arrays.asList(
            "&7Click to reset your hotbar",
            "&7settings to default"
    );
    public static Material reset_defaults_button_icon = Helper.get().getMaterialByName("BARRIER");

    // NOTE: defaults loaded on start / reload
    public static HashMap<Integer, ShopPage> hotbar_defaults = new HashMap<>();
    public static List<ShopPage> excluded_categories = new ArrayList<>();

    public static ItemStack divider_material = Helper.get().parseItemStack("gray_stained_glass_pane");
    public static ItemStack selected_slot_material = Helper.get().parseItemStack("red_stained_glass_pane");

    public static String categories_gui_title = "&a{category-name}";
    public static List<String> categories_gui_lore = Arrays.asList(
            "&7Click to add to selected slot",
            "&7select this to add to a hotbar",
            "&7slot below, to favor that slot",
            "&7when purchasing an item in this",
            "&7category, or on spawn",
            "",
            "&eClick to add to slot {selected-slot}"
    );

    public static String hotbar_gui_items_title = "&a{category-name}";
    public static List<String> hotbar_gui_items_lore = Arrays.asList(
            "&7{category-name} items will prioritize",
            "&7this slot",
            "",
            "&eClick while selected to remove"
    );

}
