package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class SInventoryInstance extends SInventoryObject implements Listener {
    public static JavaPlugin pluginHook;

    public SInventoryInstance parentInstance;
    public boolean moving = false;

    public Inventory mainInventory;
    public Player inventoryOwner = null;
    public VRender renderCache = null;

    // events
    ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryClickEvent>> onClickAsyncEvents = new ArrayList<>();

    ArrayList<Consumer<InventoryCloseEvent>> onCloseEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryCloseEvent>> onCloseAsyncEvents = new ArrayList<>();


    // main functionalities

    public void open(Player p){
        Bukkit.getScheduler().runTask(pluginHook, () -> {
//            p.closeInventory();
            registerRenderHooks(this);
            if(mainInventory == null) mainInventory = Bukkit.createInventory(null, 54);
            renderItems();
            Bukkit.getPluginManager().registerEvents(this, pluginHook);
            p.openInventory(mainInventory);
            inventoryOwner = p;
        });

    }

    public void close(boolean ignoreParent){
        Bukkit.getScheduler().runTask(pluginHook, () -> {
            if(ignoreParent){
                moving = true;
            }
            getPlayer().closeInventory();
        });
    }

    public void close(){
        close(false);
    }

    public Player getPlayer(){
        return inventoryOwner;
    }

    public void renderItems(){
        VRender render = executeRender();

        if(renderCache != null){
            for(SInventoryPosition pos: renderCache.iRender.keySet()){
                if(!render.iRender.containsKey(pos)){
                    mainInventory.setItem(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9), new ItemStack(Material.AIR));
                }
            }
        }

        for(SInventoryPosition pos: render.iRender.keySet()){
            if(renderCache != null){
                if(renderCache.iRender.containsKey(pos)) {
                    if(render.iRender.get(pos).isSimilar(renderCache.iRender.get(pos))) continue;
                }
            }
            mainInventory.setItem(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9), render.iRender.get(pos));
        }
        this.renderCache = render;
    }

    public void registerRenderHooks(SInventoryObject startingPoint){
        for(Field field: startingPoint.getClass().getDeclaredFields()){
            try{
                if(!SInventoryState.class.isAssignableFrom(field.getType())) continue;

                SInventoryState<?> state = (SInventoryState<?>) field.get(startingPoint);
                state.addOnSetEvent((e)->{
                    setRequiredRenderToTree(startingPoint);
                });
                state.addOnSetEvent((e)->{
                    Bukkit.getScheduler().runTask(SInventoryInstance.pluginHook, this::renderItems);
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for(SInventoryObject childObject: startingPoint.childObjects.values()){
            registerRenderHooks(childObject);
        }
    }

    public void unmountObjects(SInventoryObject startingPoint){
        startingPoint.onUnMount();
        for(SInventoryObject childObject: startingPoint.childObjects.values()){
            unmountObjects(childObject);
        }
    }

    public void chainOpen(SInventoryInstance nextInstance){
        moving = true;
        nextInstance.parentInstance = this;
        nextInstance.open(getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getWhoClicked().getUniqueId() != getPlayer().getUniqueId()) return;
        SInventoryPosition pos = new SInventoryPosition(e.getRawSlot()%9, e.getRawSlot()/9);
        if(!mainRender.clickEventHandling.containsKey(pos)) return;
        SInventoryObject object = mainRender.clickEventHandling.get(pos);
        if(!object.isClickable()) e.setCancelled(true);

        SInventoryObject.executeOnClick(object, e, pos);

        // instance events
        for(Consumer<InventoryClickEvent> event: onClickEvents){
            event.accept(e);
        }
        for(Consumer<InventoryClickEvent> event: onClickAsyncEvents){
            new Thread(() -> event.accept(e)).start();
        }

        // object events
        for(Consumer<InventoryClickEvent> event: object.onClickEvents){
            event.accept(e);
        }
        for(Consumer<InventoryClickEvent> event: object.asyncOnClickEvents){
            new Thread(() -> event.accept(e)).start();
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer().getUniqueId() != getPlayer().getUniqueId()) return;
        unmountObjects(this);
        cancelAllBukkitTasks(this);
        HandlerList.unregisterAll(this);

        // instance events
        for(Consumer<InventoryCloseEvent> event: onCloseEvents){
            event.accept(e);
        }
        for(Consumer<InventoryCloseEvent> event: onCloseAsyncEvents){
            new Thread(() -> event.accept(e)).start();
        }

        // linked instance
        if(!moving && parentInstance != null){
            Bukkit.getScheduler().runTaskAsynchronously(pluginHook, ()-> {
                parentInstance.open((Player) e.getPlayer());
            });
        }
        if(moving) moving = false;
    }

}
