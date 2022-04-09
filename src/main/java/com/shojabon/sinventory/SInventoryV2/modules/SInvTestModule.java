package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryPosition;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SInvTestModule extends SInventoryInstance {


    SInvButton button = new SInvButton();
    ItemStack back;

    public SInvTestModule(ItemStack background, int count){
        setClickable(false);
        this.back = background;

        setChildObject(button, "button", new SInventoryPosition(1, 1, 2));
    }

    @Override
    public void onClick(InventoryClickEvent event, SInventoryPosition relativePosition) {
        Bukkit.broadcastMessage("glass " + relativePosition.get3DString());
    }

    @Override
    public VRender render(VRender render) {
        render.set(new int[]{2, 3, 4, 5}, new int[]{2,3,4,5}, back);
        return render;

    }
}
