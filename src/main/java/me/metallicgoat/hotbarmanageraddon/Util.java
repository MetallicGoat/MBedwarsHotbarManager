package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Util {

    // I'm tired, ok
    public static Material getMaterialByNameNotNull(String name){
        final Material material = Helper.get().getMaterialByName(name);

        return material != null ? material : Material.AIR;
    }

    public static ItemStack buildItemStack(Material item, String displayName, List<String> lore, int amount){

        final ItemStack itemStack = new ItemStack(item);
        final ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(Message.build(displayName).done());

        if(lore != null){

            final List<String> formattedLore =new ArrayList<>();

            for(String s : lore)
                formattedLore.add(Message.build(s).done());

            meta.setLore(formattedLore);
        }

        itemStack.setItemMeta(meta);
        itemStack.setAmount(amount);

        return itemStack;

    }

}
