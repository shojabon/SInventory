package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SInvCounter extends SInventoryObject {

    public SInventoryState<Integer> count = new SInventoryState<>(1);

    public SInvCounter(){
        addOnClickEvent((e) -> {
            count.set(1);
        });
    }

    @Override
    public VRender render(VRender render) {
        if(count.get() == 1){
            render.set(0, 0, new ItemStack(Material.DIAMOND));
        }
        if(count.get() == 2){
            render.set(0, 1, new ItemStack(Material.GOLD_INGOT));
        }
        if(count.get() == 3){
            render.set(0, 2, new ItemStack(Material.IRON_INGOT));
        }
        if(count.get() == 4){
            render.set(0, 3, new ItemStack(Material.COPPER_INGOT));
        }
        return render;

    }
}
