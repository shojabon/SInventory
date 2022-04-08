package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class SInventoryObject {

    private ArrayList<SInventory> childObjects = new ArrayList<>();

    boolean requiredRender = false;

    int x;
    int y;
    int z;

    //events
    private ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    private ArrayList<Consumer<InventoryClickEvent>> asyncOnClickEvents = new ArrayList<>();

    private ArrayList<Runnable> onInitEvents = new ArrayList<>();
    private ArrayList<Runnable> onRenderEvents = new ArrayList<>();


    public void setOffset(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // main functions
    public VRender render(){
        return new VRender();
    }



    // events

    public void addOnClickEvent(Consumer<InventoryClickEvent> event){
        this.onClickEvents.add(event);
    }

    public void addAsynchronousOnClickEvent(Consumer<InventoryClickEvent> event){
        this.asyncOnClickEvents.add(event);
    }

    public void addOnInitEvent(Runnable event){
        this.onInitEvents.add(event);
    }

    public void addOnRenderEvent(Runnable event){
        this.onRenderEvents.add(event);
    }


}
