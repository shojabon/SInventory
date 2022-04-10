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

    public static void onEnableHook(JavaPlugin plugin){
        pluginHook = plugin;
    }

    public static void onDisableHook(){
        for(Player p: Bukkit.getOnlinePlayers()){
            movingPlayers.add(p.getUniqueId());
            p.closeInventory();
        }
        movingPlayers.clear();
    }

    public static ArrayList<UUID> movingPlayers = new ArrayList<>();

    private boolean inventoryOpen = false;

    public SInventoryInstance parentInstance;

    public Inventory mainInventory;
    public String title;
    public int rows = 6;

    private Player inventoryOwner = null;
    public VRender renderCache = null;

    // events
    ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryClickEvent>> AsyncOnClickEvents = new ArrayList<>();

    ArrayList<Consumer<InventoryCloseEvent>> onCloseEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryCloseEvent>> asyncOnCloseEvents = new ArrayList<>();

    public void addInventoryOnClickEvent(Consumer<InventoryClickEvent> e){
        onClickEvents.add(e);
    }
    public void addInventoryAsyncOnClickEvent(Consumer<InventoryClickEvent> e){
        asyncOnClickEvents.add(e);
    }
    public void addOnCloseEvent(Consumer<InventoryCloseEvent> e){
        onCloseEvents.add(e);
    }
    public void addAsyncOnCloseEvent(Consumer<InventoryCloseEvent> e){
        asyncOnCloseEvents.add(e);
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setRows(int rows){
        this.rows = rows;
    }

    // main functionalities

    public void open(Player p){
        Bukkit.getScheduler().runTask(pluginHook, () -> {
            try{
                registerRenderHooks(this);
                if(mainInventory == null) {
                    if(title == null){
                        mainInventory = Bukkit.createInventory(null, rows*9);
                    }else{
                        mainInventory = Bukkit.createInventory(null, rows*9, this.title);
                    }
                }
                p.openInventory(mainInventory);
                Bukkit.getPluginManager().registerEvents(this, pluginHook);
                inventoryOpen = true;
                renderItems();
                inventoryOwner = p;
            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }

    public void close(boolean ignoreParent){
        Bukkit.getScheduler().runTask(pluginHook, () -> {
            if(!ignoreParent && parentInstance != null && !movingPlayers.contains(getPlayer().getUniqueId())){
                movingPlayers.add(getPlayer().getUniqueId());
                parentInstance.open(getPlayer());
            }else {
                getPlayer().closeInventory();
            }
        });
    }

    public void close(){
        close(false);
    }

    public Player getPlayer(){
        return inventoryOwner;
    }

    public void renderItems(){
        if(!inventoryOpen) return;
        Bukkit.getScheduler().runTask(pluginHook, ()->{
            VRender render = executeRender();

            if(renderCache != null){
                for(SInventoryPosition pos: renderCache.iRender.keySet()){
                    if(!render.iRender.containsKey(pos)){
                        if(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9) > rows*9) continue;
                        mainInventory.setItem(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9), new ItemStack(Material.AIR));
                    }
                }
            }

            for(SInventoryPosition pos: render.iRender.keySet()){
                if(renderCache != null){
                    if(renderCache.iRender.containsKey(pos)) {
                        if(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9) > rows*9) continue;
                        if(render.iRender.get(pos).isSimilar(renderCache.iRender.get(pos)) &&
                                render.iRender.get(pos).getAmount() == renderCache.iRender.get(pos).getAmount()) continue;
                    }
                }
                mainInventory.setItem(pos.x + objectLocation.x + ((pos.y + objectLocation.y) * 9), render.iRender.get(pos));
            }
            this.renderCache = render;
        });
    }

    public void registerRenderHooks(SInventoryObject startingPoint){
        for(Field field: startingPoint.getClass().getDeclaredFields()){
            try{
                if(!SInventoryState.class.isAssignableFrom(field.getType())) continue;

                SInventoryState<?> state = (SInventoryState<?>) field.get(startingPoint);
                state.onSetRenderEvent = (e) -> {
                    setRequiredRenderToTree(startingPoint);
                    this.renderItems();
                };
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for(SInventoryObject childObject: startingPoint.childObjects){
            registerRenderHooks(childObject);
        }
    }

    public void unmountObjects(SInventoryObject startingPoint){
        try{
            startingPoint.onUnMount();
        }catch (Exception e){
            e.printStackTrace();
        }
        for(SInventoryObject childObject: startingPoint.childObjects){
            unmountObjects(childObject);
        }
    }

    public void chainOpen(SInventoryInstance nextInstance){
        Bukkit.getScheduler().runTask(pluginHook, () ->{
            movingPlayers.add(getPlayer().getUniqueId());
            nextInstance.parentInstance = this;
            nextInstance.open(getPlayer());
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getWhoClicked().getUniqueId() != getPlayer().getUniqueId()) return;
        if(!inventoryOpen) return;

        SInventoryPosition pos = new SInventoryPosition(e.getRawSlot()%9, e.getRawSlot()/9);
        if(!mainRender.clickEventHandling.containsKey(pos)) return;
        SInventoryObject object = mainRender.clickEventHandling.get(pos);
        pos.z = object.absoluteLocation.z;
        if(!object.isClickable()) e.setCancelled(true);


        SInventoryObject.executeOnClick(object, e, pos);


        // position specific events
        for(SInventoryPosition eventPosition: object.positionOnClickEvents.keySet()){
            if(!eventPosition.isTwoDEquals(pos)) continue;
            for(Consumer<InventoryClickEvent> event: object.positionOnClickEvents.get(eventPosition)){
                try{event.accept(e);}catch (Exception exception){exception.printStackTrace();}
            }
        }

        for(SInventoryPosition eventPosition: object.asyncPositionOnClickEvents.keySet()){
            if(!eventPosition.isTwoDEquals(pos)) continue;
            for(Consumer<InventoryClickEvent> event: object.asyncPositionOnClickEvents.get(eventPosition)){
                new Thread(() -> event.accept(e)).start();
            }
        }

        // instance events
        for(Consumer<InventoryClickEvent> event: onClickEvents){
            try{event.accept(e);}catch (Exception exception){exception.printStackTrace();}
        }
        for(Consumer<InventoryClickEvent> event: AsyncOnClickEvents){
            new Thread(() -> event.accept(e)).start();
        }

        // object events
        for(Consumer<InventoryClickEvent> event: object.onClickEvents){
            try{event.accept(e);}catch (Exception exception){exception.printStackTrace();}
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
            try{event.accept(e);}catch (Exception exception){exception.printStackTrace();}
        }
        for(Consumer<InventoryCloseEvent> event: asyncOnCloseEvents){
            new Thread(() -> event.accept(e)).start();
        }

        // linked instance
        if(!movingPlayers.contains(getPlayer().getUniqueId()) && parentInstance != null){
            Bukkit.getScheduler().runTaskAsynchronously(pluginHook, ()-> {
                parentInstance.open((Player) e.getPlayer());
            });
        }
        movingPlayers.remove(getPlayer().getUniqueId());
        inventoryOpen = false;
    }

}
