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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SInventoryObject{

    public SInventoryObject(){
        onMount();
    }

    HashMap<String, SInventoryObject> childObjects = new HashMap<>();
    VRender mainRender = new VRender(this);
    SInventoryObject parentObject;

    boolean requiredRender = true;

    SInventoryPosition objectLocation = new SInventoryPosition(0, 0, 0);
    SInventoryPosition absoluteLocation = new SInventoryPosition(0, 0, 0);

    //events
    ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryClickEvent>> asyncOnClickEvents = new ArrayList<>();

    ArrayList<BukkitTask> bukkitTasks = new ArrayList<>();

    private boolean clickable = false;

    public final void setClickable(boolean clickable){
        this.clickable = clickable;
    }

    public final boolean isClickable(){
        return clickable;
    }

    public final void setOffset(int x, int y, int z){
        objectLocation = new SInventoryPosition(x, y, z);
        absoluteLocation = new SInventoryPosition(x, y, z);
    }

    // main functions
    public VRender render(VRender render){
        return render;
    }

    public void onMount(){}

    public void onUnMount(){}

    public void onClick(InventoryClickEvent event, SInventoryPosition relativePosition){}

    public static void executeOnClick(SInventoryObject starting, InventoryClickEvent event, SInventoryPosition clickLocation){
        SInventoryPosition relativePosition = SInventoryPosition.minus(clickLocation, starting.absoluteLocation, true);
        relativePosition.z = starting.absoluteLocation.z;
        starting.onClick(event, relativePosition);
        if(starting.parentObject != null) executeOnClick(starting.parentObject, event, clickLocation);
    }

    public final VRender executeRender(){
        if(this.requiredRender){
            this.mainRender = this.render(new VRender(this));
            this.requiredRender = false;
        }
        VRender result = this.mainRender.copy();
        for(SInventoryObject childObject: this.childObjects.values()){
            VRender childRender = childObject.executeRender();
            result.mergeRender(childRender);

        }

        return result;
    }

    public static void setRequiredRenderToTree(SInventoryObject startingPoint){
        startingPoint.requiredRender = true;
        if(startingPoint.parentObject != null) setRequiredRenderToTree(startingPoint.parentObject);
    }

    public final  void setChildObject(SInventoryObject object, String name, SInventoryPosition position){
        object.setOffset(position.x, position.y, position.z);
        object.absoluteLocation = new SInventoryPosition(position.x + objectLocation.x, position.y +objectLocation.y, position.z + objectLocation.z);
        object.parentObject = this;
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

    // timer tasks

    public final void runTaskTimer(Runnable task, int delayTick, int intervalTick){
        bukkitTasks.add(Bukkit.getScheduler().runTaskTimer(SInventoryInstance.pluginHook, task, delayTick, intervalTick));
    }

    public final void runTaskTimerAsync(Runnable task, int delayTick, int intervalTick){
        bukkitTasks.add(Bukkit.getScheduler().runTaskTimerAsynchronously(SInventoryInstance.pluginHook, task, delayTick, intervalTick));
    }


    public final void runTaskLater(Runnable task, int delayTick, boolean async){
        bukkitTasks.add(Bukkit.getScheduler().runTaskLater(SInventoryInstance.pluginHook, task, delayTick));
    }

    public final void runTaskLaterAsync(Runnable task, int delayTick, boolean async){
        bukkitTasks.add(Bukkit.getScheduler().runTaskLaterAsynchronously(SInventoryInstance.pluginHook, task, delayTick));
    }

    public static void cancelAllBukkitTasks(SInventoryObject startingPoint){
        for(BukkitTask task: startingPoint.bukkitTasks){
            task.cancel();
        }
        for(SInventoryObject obj: startingPoint.childObjects.values()){
            cancelAllBukkitTasks(obj);
        }
    }


}
