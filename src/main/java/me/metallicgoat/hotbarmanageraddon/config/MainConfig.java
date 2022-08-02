package me.metallicgoat.hotbarmanageraddon.config;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.hotbarmanageraddon.HotbarManagerPlugin;
import me.metallicgoat.hotbarmanageraddon.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainConfig {

    public static final byte VERSION = 0;
    public static int CURRENT_VERSION = -1;

    private static File getFile() {
        return new File(HotbarManagerPlugin.getAddon().getDataFolder(), "config.yml");
    }

    public static void load() {
        synchronized (MainConfig.class) {
            try {
                loadUnchecked();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadUnchecked() throws Exception {
        final File file = getFile();

        if (!file.exists()) {
            save();
            return;
        }

        // load it
        final FileConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read it
        // TODO improve this messy ass reading
        ConfigValue.close_button_icon = getMaterialByName(config, "Close-Button.Icon", "ARROW");
        ConfigValue.close_button_title = config.getString("Close-Button.Title", ConfigValue.close_button_title);
        if(config.contains("Close-Button.Lore"))
            ConfigValue.close_button_lore = config.getStringList("Close-Button.Lore");

        ConfigValue.reset_defaults_button_icon = getMaterialByName(config, "Reset-Defaults-Button.Icon", "BARRIER");
        ConfigValue.reset_defaults_button_title = config.getString("Reset-Defaults-Button.Title", ConfigValue.reset_defaults_button_title);
        if(config.contains("Reset-Defaults-Button.Lore"))
            ConfigValue.reset_defaults_button_lore = config.getStringList("Reset-Defaults-Button.Lore");

        ConfigValue.categories_gui_title = config.getString("Category-Items.Title", ConfigValue.categories_gui_title);
        if(config.contains("Category-Items.Lore"))
            ConfigValue.categories_gui_lore = config.getStringList("Category-Items.Lore");

        ConfigValue.hotbar_gui_items_title = config.getString("Hotbar-Items.Title", ConfigValue.hotbar_gui_items_title);
        if(config.contains("Hotbar-Items.Lore"))
            ConfigValue.hotbar_gui_items_lore = config.getStringList("Hotbar-Items.Lore");

        ConfigValue.divider_material = new ItemStack(getMaterialByName(config, "Divider-Material", "gray_stained_glass_pane"));
        ConfigValue.selected_slot_material = new ItemStack(getMaterialByName(config, "Selected-Slot-Material", "red_stained_glass_pane"));

        {
            if(config.contains("Excluded-Categories")){

                final List<String> allExcludedStrings = config.getStringList("Excluded-Categories");
                final List<ShopPage> allExcluded = new ArrayList<>();

                for(String excluded : allExcludedStrings){
                    for(ShopPage page : GameAPI.get().getShopPages()){

                        if(page.getName().equalsIgnoreCase(excluded)
                                || page.getDisplayName().equalsIgnoreCase(excluded)
                                || page.getIcon().getType().name().equalsIgnoreCase(excluded)) {

                            allExcluded.add(page);
                            break;
                        }
                    }
                }

                ConfigValue.excluded_categories = allExcluded;
            }
        }

        {
            if(config.contains("Default-Hotbar")){

                final HashMap<Integer, ShopPage> defaults = new HashMap<>();

                for(String def : config.getStringList("Default-Hotbar")){

                    final String[] split = def.split(":");

                    if(split.length != 2)
                        continue;

                    final Integer slot = Helper.get().parseInt(split[0]);

                    if(slot == null || slot > 8 || slot < 0)
                        continue;

                    for(ShopPage page : GameAPI.get().getShopPages()){

                        if(page.getName().equalsIgnoreCase(split[1])
                                || page.getDisplayName().equalsIgnoreCase(split[1])
                                || page.getIcon().getType().name().equalsIgnoreCase(split[1])) {

                            defaults.put(slot - 1, page);
                            break;
                        }
                    }
                }

                ConfigValue.hotbar_defaults = defaults;
            }
        }

        // auto update file if newer version
        CURRENT_VERSION = config.getInt("file-version", -1);

        if (CURRENT_VERSION != VERSION)
            save();


    }

    private static void save() throws Exception {

        final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

        config.addComment("Used for auto-updating the config file. Ignore it");
        config.set("file-version", VERSION);

        config.addEmptyLine();

        config.addComment("Hotbar Manager close button");
        config.set("Close-Button.Icon", ConfigValue.close_button_icon.name());
        config.set("Close-Button.Title", ConfigValue.close_button_title);
        config.set("Close-Button.Lore", ConfigValue.close_button_lore);

        config.addEmptyLine();

        config.addComment("Hotbar Manager reset defaults button");
        config.set("Reset-Defaults-Button.Icon", ConfigValue.reset_defaults_button_icon.name());
        config.set("Reset-Defaults-Button.Title", ConfigValue.reset_defaults_button_title);
        config.set("Reset-Defaults-Button.Lore", ConfigValue.reset_defaults_button_lore);

        config.addEmptyLine();

        config.addComment("Customize text on category items in the Hotbar Manager GUI");
        config.set("Category-Items.Title", ConfigValue.categories_gui_title);
        config.set("Category-Items.Lore", ConfigValue.categories_gui_lore);

        config.addEmptyLine();

        config.addComment("Customize text on selected hotbar items in the Hotbar Manager GUI");
        config.set("Hotbar-Items.Title", ConfigValue.hotbar_gui_items_title);
        config.set("Hotbar-Items.Lore", ConfigValue.hotbar_gui_items_lore);

        config.addEmptyLine();

        config.addComment("The material used to separate the hotbar from the category selection buttons in the Hotbar Manager GUI");
        config.set("Divider-Material", ConfigValue.divider_material.getType().name());

        config.addEmptyLine();

        config.addComment("The material used to highlight the selected slot in the Hotbar Manager GUI");
        config.set("Selected-Slot-Material", ConfigValue.selected_slot_material.getType().name());

        config.addEmptyLine();

        config.addComment("Set categories excluded from Hotbar Manager");
        config.addComment("Add either category name OR category icon material name");
        {
            final List<String> excluded = new ArrayList<>();

            for(ShopPage page : ConfigValue.excluded_categories)
                excluded.add(page.getIcon().getType().name());

            config.set("Excluded-Categories", excluded);

        }

        config.addEmptyLine();

        config.addComment("Set the default Hotbar layout that is used");
        config.addComment("Usage: <slot number>:<icon material type OR category name>");
        {
            final List<String> defaults = new ArrayList<>();

            for(Map.Entry<Integer, ShopPage> entry : ConfigValue.hotbar_defaults.entrySet())
                defaults.add((entry.getKey() + 1) + ":" + entry.getValue().getIcon().getType().name());

            config.set("Default-Hotbar", defaults);
        }


        config.save(getFile());
    }

    private static Material getMaterialByName(FileConfiguration config, String path, String def){

        final String matName = config.getString(path, def);
        final Material material = Helper.get().getMaterialByName(matName);

        return material != null ? material : Material.AIR;
    }

}