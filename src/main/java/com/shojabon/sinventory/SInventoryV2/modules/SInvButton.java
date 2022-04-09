package com.shojabon.sinventory.SInventoryV2.modules;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryObject;
import com.shojabon.sinventory.SInventoryV2.SInventoryState;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class SInvButton extends SInventoryObject{

    @Override
    public VRender render(VRender render) {
        render.set(0, 0, new ItemStack(new ItemStack(Material.WOODEN_AXE)));
        return render;

    }
}
