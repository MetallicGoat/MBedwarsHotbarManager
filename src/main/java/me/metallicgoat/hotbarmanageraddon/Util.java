package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static ItemStack buildItemStack(Material item, String displayName, List<String> lore, List<Pair<String, String>> placeholders, int amount){

        final ItemStack itemStack = NMSHelper.get().hideAttributes(new ItemStack(item));
        ItemMeta meta = itemStack.getItemMeta();

        if(meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(item);

        meta.setDisplayName(Message.build(displayName).done());

        if(lore != null){

            final List<String> formattedLore =new ArrayList<>();

            for(String s : lore) {

                final Message message = Message.build(s);

                if(placeholders != null) {
                    for (Pair<String, String> placeholder : placeholders)
                        message.placeholder(placeholder.getKey(), placeholder.getValue());
                }

                formattedLore.add(message.done());
            }

            meta.setLore(formattedLore);
        }

        itemStack.setItemMeta(meta);
        itemStack.setAmount(amount);

        return itemStack;

    }

}
