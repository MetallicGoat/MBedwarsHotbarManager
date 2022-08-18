package me.metallicgoat.hotbarmanageraddon;

import de.marcely.bedwars.tools.Helper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorSet {

    private ItemStack boots;
    private ItemStack leggings;
    private ItemStack chestplate;
    private ItemStack helmet;


    public void setArmor(ItemStack itemStack){

        if(itemStack.getType().name().contains("BOOTS")){
            if(boots == null || isArmorBetter(itemStack, boots))
                boots = itemStack;
        }

        if(itemStack.getType().name().contains("LEGGINGS")){
            if(leggings == null || isArmorBetter(itemStack, leggings))
                leggings = itemStack;
        }

        if(itemStack.getType().name().contains("CHESTPLATE")){
            if(chestplate == null || isArmorBetter(itemStack, chestplate))
                chestplate = itemStack;
        }

        if(itemStack.getType().name().contains("HELMET")){
            if(helmet == null || isArmorBetter(itemStack, helmet))
                helmet = itemStack;
        }

    }

    public void wearArmor(Player player){

        if(boots != null)
            Helper.get().setPlayerArmor(player, boots, true);

        if(leggings != null)
            Helper.get().setPlayerArmor(player, leggings, true);

        if(chestplate != null)
            Helper.get().setPlayerArmor(player, chestplate, true);

        if(helmet != null)
            Helper.get().setPlayerArmor(player, helmet, true);

    }

    private boolean isArmorBetter(ItemStack newStack, ItemStack oldStack){
        return getArmorLevel(newStack) > getArmorLevel(oldStack);
    }

    private int getArmorLevel(ItemStack armor){

        final String armorName = armor.getType().name();

        if(armorName.contains("LEATHER")){
            return 1;
        }else if(armorName.contains("CHAINMAIL")){
            return 2;
        }else if(armorName.contains("GOLD")){
            return 3;
        }else if(armorName.contains("IRON")){
            return 4;
        }else if(armorName.contains("DIAMOND")){
            return 5;
        }else if(armorName.contains("NETHERITE")){
            return 6;
        }else{
            return 0;
        }
    }
}
