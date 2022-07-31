package me.metallicgoat.hotbarmanageraddon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.gui.CenterFormat;
import de.marcely.bedwars.tools.gui.ClickListener;
import de.marcely.bedwars.tools.gui.ClickableGUI;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import me.metallicgoat.hotbarmanageraddon.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotBarManagementSession {

    private final Player player;
    private final boolean inShop;
    private int selectedSlot = 0;
    private HashMap<Integer, String> categorySlotMap = new HashMap<>();

    // TODO make click pattern more intuitive

    public HotBarManagementSession(Player player, boolean inShop){
        this.player = player;
        this.inShop = inShop;
        loadHotBarData();
    }

    private void openGUI(){
        buildHotBarManagerGUI().open(player);
    }

    private ClickableGUI buildHotBarManagerGUI(){

        final ClickableGUI gui = new ChestGUI(6, Message.build(ConfigValue.gui_title).done());

        gui.setItem(new GUIItem(new ItemStack(Material.ARROW), new ClickListener() {
            @Override
            public void onClick(Player player, boolean b, boolean b1) {

                player.closeInventory();

                // TODO for when we add button in shop
                if(inShop)
                    GameAPI.get().openShop(player);

            }
        }), 3, 0);

        gui.setItem(new GUIItem(new ItemStack(Material.BARRIER), new ClickListener() {
            @Override
            public void onClick(Player player, boolean b, boolean b1) {
                // TODO
            }
        }), 5, 0);


        // Categories
        final Collection<ShopPage> pages = GameAPI.get().getShopPages();

        int i = 0;

        for(ShopPage page : pages){
            if(page.getIcon().getType() != Material.IRON_CHESTPLATE) {
                gui.setItem(new GUIItem(page.getIcon(), categoryClickListener(page.getName())), i, 2);
                i++;
            }
        }

        gui.formatRow( 2, CenterFormat.CENTRALIZED);

        Integer selectedSlotX = null;

        // Slots
        for(Map.Entry<Integer, String> entry : categorySlotMap.entrySet()){
            if(entry.getKey() == null || entry.getValue() == null)
                continue;

            final ItemStack icon = getHotBarIcon(entry.getValue());

            if(icon == null)
                continue;

            gui.setItem(new GUIItem(icon, hotBarClickListener(entry.getKey())), entry.getKey(), 4);
        }

        i = 0;
        while(i < 9){

            if(gui.getItem(i, 4) == null)
                gui.setItem(new GUIItem(new ItemStack(Material.AIR), hotBarClickListener(i)), i, 4);

            if(selectedSlot == i)
                selectedSlotX = i;

            i++;
        }


        // Top Divider
        drawDivider(gui, selectedSlotX, 3);

        // Bottom Divider
        drawDivider(gui, selectedSlotX, 5);

        return gui;
    }

    private void drawDivider(ClickableGUI gui, Integer selectedSlotX, int y){
        int i = 0;

        while(i < 9){

            if(selectedSlotX != null && selectedSlotX == i){
                gui.setItem(ConfigValue.selected_slot_material, i, y, hotBarClickListener(i));
                i++;
                continue;
            }

            gui.setItem(ConfigValue.divider_material, i, y, hotBarClickListener(i));
            i++;
        }
    }

    private ItemStack getHotBarIcon(String shopPage){
        for(ShopPage page : GameAPI.get().getShopPages()){
            if(shopPage.equals(page.getName()))
                return page.getIcon();
        }

        return null;
    }

    private ClickListener categoryClickListener(String pageName){

        return new ClickListener() {
            @Override
            public void onClick(Player player, boolean leftClick, boolean shiftClick) {

                if(!isChange(selectedSlot, pageName))
                    return;

                categorySlotMap.put(selectedSlot, pageName);
                saveHotBarData();
                openGUI();
            }
        };
    }

    private boolean isChange(int selectedSlot, String pageName){

        for(Map.Entry<Integer, String> entry : categorySlotMap.entrySet()){
            if(entry.getKey() == selectedSlot && entry.getValue().equals(pageName)){
                return false;
            }
        }

        return true;
    }

    private ClickListener hotBarClickListener(int slot){

        return new ClickListener() {
            @Override
            public void onClick(Player player, boolean leftClick, boolean shiftClick) {

                if(shiftClick){
                    categorySlotMap.remove(slot);
                    saveHotBarData();
                    openGUI();
                    return;
                }

                if(selectedSlot != slot){
                    selectedSlot = slot;
                    openGUI();
                }
            }
        };
    }

    private void loadHotBarData(){

        PlayerDataAPI.get().getProperties(player.getUniqueId(), playerProperties -> {
            final Optional<String> json = playerProperties.get("hotbar_manager");

            if(!json.isPresent()) {
                openGUI();
                return;
            }

            final Gson gson = new Gson();
            categorySlotMap = gson.fromJson(json.get(), new TypeToken<HashMap<Integer, String>>(){}.getType());

            openGUI();
        });
    }

    private void saveHotBarData(){

        final Gson gson = new Gson();
        String json = gson.toJson(categorySlotMap);

        PlayerDataAPI.get().getProperties(player.getUniqueId(), playerProperties -> playerProperties.set("hotbar_manager", json));
    }
}
