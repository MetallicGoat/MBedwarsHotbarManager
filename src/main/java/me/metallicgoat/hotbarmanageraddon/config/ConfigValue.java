package me.metallicgoat.hotbarmanageraddon.config;

import de.marcely.bedwars.tools.Helper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConfigValue {

    public static final String gui_title = "Hotbar Manager";

    // TODO parse all these as one itemstack, this is dumb
    public static final String close_button_title = "Close";
    public static final List<String> close_button_lore = Collections.singletonList("Exit Menu");
    public static final Material close_button_icon = Helper.get().getMaterialByName("ARROW");

    // TODO parse all these as one itemstack, this is dumb
    public static final String reset_defaults_button_title = "Reset";
    public static final List<String> reset_defaults_button_lore = Collections.singletonList("Reset settings to defaults");
    public static final Material reset_defaults_button_icon = Helper.get().getMaterialByName("BARRIER");

    //TODO maybe load on start
    public final HashMap<Integer, String> hotbar_defaults = new HashMap<Integer, String>(){{
        put(1, "");
    }};
    // TODO option to exclude categories (Maybe save allowed categories only)

    public static final ItemStack divider_material = Helper.get().parseItemStack("gray_stained_glass_pane");
    public static final ItemStack selected_slot_material = Helper.get().parseItemStack("red_stained_glass_pane");


}
