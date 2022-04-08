package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VRender {

    public HashMap<String, ItemStack> iRender = new HashMap<>();

    public void set(int x, int y, ItemStack item){
        iRender.put(x + "," + y, item);
    }

    public HashMap<String, ItemStack> get(int offsetX, int offsetY){
        HashMap<String, ItemStack> result = new HashMap<>();
        return result;
    }

}
