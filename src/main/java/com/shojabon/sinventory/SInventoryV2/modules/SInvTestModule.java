package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryPosition;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SInvTestModule extends SInventoryInstance {


    SInvButton button = new SInvButton();
    SInvCounter counter = new SInvCounter();
    SInvCounter counter2 = new SInvCounter();
    int i = 0;

    public SInvTestModule(){
        setClickable(false);
        button.addOnClickEvent((e) -> {
            if(i == 0){
                counter.item.set(new ItemStack(Material.IRON_INGOT));
            }
            if(i == 1){
                counter.item.set(new ItemStack(Material.GOLD_INGOT));
            }
            if(i == 2){
                counter.item.set(new ItemStack(Material.COPPER_INGOT));
            }
            i+=1;
            if(i == 3){
                i = 1;
            }
        });


        setChildObject(counter, "counter", new SInventoryPosition(0, 1, 0));
        setChildObject(counter2, "counter2", new SInventoryPosition(1, 1, 0));
        setChildObject(button, "button", new SInventoryPosition(0, 0, 0));
    }

    @Override
    public VRender render(VRender render) {
        return render;

    }
}
