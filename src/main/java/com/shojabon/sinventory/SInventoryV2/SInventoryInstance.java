package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class SInventoryInstance extends SInventoryObject{
    public static JavaPlugin pluginHook;
    public static HashMap<UUID, SInventoryInstance> activeInstances = new HashMap<>();
    public Inventory mainInventory;
    public Player inventoryOwner = null;
    public VRender renderCache = null;

    public void open(Player p){
        registerRenderHooks(this);
        mainInventory = Bukkit.createInventory(null, 54);
        renderItems();
        p.openInventory(mainInventory);
        inventoryOwner = p;
        activeInstances.put(p.getUniqueId(), this);
        Bukkit.getPluginManager().registerEvents(this, pluginHook);
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
                    startingPoint.setRequiredRenderToTree(startingPoint);
                });
                state.addOnSetEvent((e)->{
                    renderItems();
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

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getWhoClicked().getUniqueId() != inventoryOwner.getUniqueId()) return;
        SInventoryPosition pos = new SInventoryPosition(e.getRawSlot()%9, e.getRawSlot()/9);
        if(!mainRender.clickEventHandling.containsKey(pos)) return;
        SInventoryObject object = mainRender.clickEventHandling.get(pos);
        if(!object.isClickable()) e.setCancelled(true);
        for(Consumer<InventoryClickEvent> event: object.onClickEvents){
            event.accept(e);
        }
        for(Consumer<InventoryClickEvent> event: object.asyncOnClickEvents){
            new Thread(() -> event.accept(e)).start();
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer().getUniqueId() != inventoryOwner.getUniqueId()) return;
        HandlerList.unregisterAll(this);
        unmountObjects(this);
    }
}
