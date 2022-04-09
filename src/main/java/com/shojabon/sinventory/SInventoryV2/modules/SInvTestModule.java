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

    public SInvTestModule(){
        setClickable(false);
        button.addOnClickEvent((e) -> {
            counter.count.set(counter.count.get() + 1);
        });


        setChildObject(counter, "counter", new SInventoryPosition(0, 1, 0));
        setChildObject(button, "button", new SInventoryPosition(0, 0, 0));
    }

    @Override
    public VRender render(VRender render) {
        return render;

    }
}
