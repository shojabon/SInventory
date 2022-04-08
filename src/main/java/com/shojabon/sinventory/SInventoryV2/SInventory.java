package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class SInventory {

    public HashMap<String, ItemStack> iRender = new HashMap<>();
    private Inventory mainInventory = null;
    public ArrayList<Integer> dirtySlots = new ArrayList<>();

    SInventoryObject mainObject = null;


    public void render(){

    }

    public void open(Player p){

    }

}
