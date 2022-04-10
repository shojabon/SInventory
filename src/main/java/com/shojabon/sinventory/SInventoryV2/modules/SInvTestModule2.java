package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryPosition;
import com.shojabon.sinventory.SInventoryV2.SInventoryState;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SInvTestModule2 extends SInventoryInstance {


    SInvButton button = new SInvButton();
    public SInventoryState<Integer> test = new SInventoryState<>(2);

    public SInvTestModule2(){

        setClickable(false);
        setRows(6);
        setTitle("test");

        button.r = () -> {
            close();
        };

        setChildObject(button, "button", new SInventoryPosition(0, 0, 2));
    }

    @Override
    public void onClick(InventoryClickEvent event, SInventoryPosition relativePosition) {
        Bukkit.broadcastMessage("glass " + relativePosition.get3DString());
    }

    @Override
    public VRender render(VRender render) {
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        back.setAmount(test.get());
        render.set(new int[]{0, 1, 2, 3}, new int[]{0, 1, 2}, back);
        return render;

    }
}
