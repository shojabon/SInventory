package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SInvCounter extends SInventoryObject {

    public SInventoryState<Integer> count = new SInventoryState<>(1);
    public SInventoryState<ItemStack> item = new SInventoryState<>(new ItemStack(Material.DIAMOND));

    public SInvCounter(){
        runTaskTimerAsync(()-> {
            if(count.get() == 1){
                count.set(5);
            }else{
                count.set(1);
            }
        }, 5, 5);
        addOnClickEvent(e -> {
            Bukkit.broadcastMessage("test");
        });
    }

    @Override
    public VRender render(VRender render) {
        for(int i = 0; i < count.get(); i++){
            render.set(0, i, new ItemStack(item.get()));
        }
        return render;

    }
}
