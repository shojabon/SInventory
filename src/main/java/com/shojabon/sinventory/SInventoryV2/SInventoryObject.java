package com.shojabon.sinventory.SInventoryV2;

import com.shojabon.sinventory.SInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SInventoryObject implements Listener {

    public SInventoryObject(){
        onMount();
    }

    HashMap<String, SInventoryObject> childObjects = new HashMap<>();
    VRender mainRender = new VRender(this);

    boolean requiredRender = true;


    SInventoryPosition objectLocation = new SInventoryPosition(0, 0, 0);

    //events
    ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryClickEvent>> asyncOnClickEvents = new ArrayList<>();

    private boolean clickable = false;

    public final void setClickable(boolean clickable){
        this.clickable = clickable;
    }
    public final boolean isClickable(){
        return clickable;
    }

    public final void setOffset(int x, int y, int z){
        objectLocation = new SInventoryPosition(x, y, z);
    }

    // main functions
    public VRender render(VRender render){
        return render;
    }

    public void onMount(){}

    public void onUnMount(){}

    public final VRender executeRender(){
        if(this.requiredRender){
            this.mainRender = this.render(new VRender(this));
            this.requiredRender = false;
        }
        VRender result = this.mainRender;
        for(SInventoryObject childObject: this.childObjects.values()){
            VRender childRender = childObject.executeRender();
            result.mergeRender(childRender);

        }

        return result;
    }

    public final  void setChildObject(SInventoryObject object, String name, SInventoryPosition position){
        object.setOffset(position.x, position.y, position.z);
        object.onMount();
        this.childObjects.put(name, object);
    }

    // events

    public final void addOnClickEvent(Consumer<InventoryClickEvent> event){
        this.onClickEvents.add(event);
    }

    public final void addAsynchronousOnClickEvent(Consumer<InventoryClickEvent> event){
        this.asyncOnClickEvents.add(event);
    }


}
