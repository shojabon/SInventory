package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class SInventoryObject{

    ArrayList<SInventoryObject> childObjects = new ArrayList<>();
    VRender mainRender = new VRender(this);
    SInventoryObject parentObject;

    boolean requiredRender = true;

    Runnable renderFunction = null;

    SInventoryPosition objectLocation = new SInventoryPosition(0, 0, 0);
    SInventoryPosition absoluteLocation = new SInventoryPosition(0, 0, 0);

    //events
    ArrayList<Consumer<InventoryClickEvent>> onClickEvents = new ArrayList<>();
    ArrayList<Consumer<InventoryClickEvent>> asyncOnClickEvents = new ArrayList<>();

    HashMap<SInventoryPosition, ArrayList<Consumer<InventoryClickEvent>>> positionOnClickEvents = new HashMap<>();
    HashMap<SInventoryPosition, ArrayList<Consumer<InventoryClickEvent>>> asyncPositionOnClickEvents = new HashMap<>();

    ArrayList<BukkitTask> bukkitTasks = new ArrayList<>();

    private boolean clickable = true;

    // properties

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

    public void onUnMount(){}

    public void onClick(InventoryClickEvent event, SInventoryPosition relativePosition){}

    public static void executeOnClick(SInventoryObject starting, InventoryClickEvent event, SInventoryPosition clickLocation){
        SInventoryPosition relativePosition = SInventoryPosition.minus(clickLocation, starting.absoluteLocation, true);
        relativePosition.z = clickLocation.z;
        starting.onClick(event, relativePosition);
        if(starting.parentObject != null) executeOnClick(starting.parentObject, event, clickLocation);
    }

    public final VRender executeRender(){
        if(this.requiredRender){
            this.mainRender = this.render(new VRender(this));
            this.requiredRender = false;
        }
        VRender result = this.mainRender.copy();
        for(SInventoryObject childObject: this.childObjects){
            VRender childRender = childObject.executeRender();
            result.mergeRender(childRender);

        }

        return result;
    }

    public static void setRequiredRenderToTree(SInventoryObject startingPoint){
        startingPoint.requiredRender = true;
        if(startingPoint.parentObject != null) setRequiredRenderToTree(startingPoint.parentObject);
    }

    public void invokeRender(){
        renderFunction.run();
    }

    public final  void setChildObject(SInventoryObject object, int x, int y, int z){
        setChildObject(object, new int[]{x}, new int[]{y}, new int[]{z});
    }

    public final  void setChildObject(SInventoryObject object, int[] x, int[] y, int[] z){
        for(int iX: x){
            for(int iY: y){
                for(int iZ: z){
                    object.setOffset(iX, iY, iZ);
                    object.absoluteLocation = new SInventoryPosition(iX + objectLocation.x, iY +objectLocation.y, iZ + objectLocation.z);
                    object.parentObject = this;
                    this.childObjects.add(object);
                }
            }
        }
    }

    // events

    public final void addOnClickEvent(Consumer<InventoryClickEvent> event){
        this.onClickEvents.add(event);
    }

    public final void addAsyncOnClickEvent(Consumer<InventoryClickEvent> event){
        this.asyncOnClickEvents.add(event);
    }

    public final void addOnClickEvent(int x, int y, Consumer<InventoryClickEvent> event){
        SInventoryPosition pos = new SInventoryPosition(x, y);
        if(!positionOnClickEvents.containsKey(pos)){
            positionOnClickEvents.put(pos, new ArrayList<>());
        }
        positionOnClickEvents.get(pos).add(event);
    }

    public final void addAsyncOnClickEvent(int x, int y, Consumer<InventoryClickEvent> event){
        SInventoryPosition pos = new SInventoryPosition(x, y);
        if(!asyncPositionOnClickEvents.containsKey(pos)){
            asyncPositionOnClickEvents.put(pos, new ArrayList<>());
        }
        asyncPositionOnClickEvents.get(pos).add(event);
    }

    // timer tasks

    public final BukkitTask runTaskTimer(Runnable task, int delayTick, int intervalTick){
        BukkitTask createdTask = Bukkit.getScheduler().runTaskTimer(SInventoryInstance.pluginHook, task, delayTick, intervalTick);
        bukkitTasks.add(createdTask);
        return createdTask;
    }

    public final BukkitTask runTaskTimerAsync(Runnable task, int delayTick, int intervalTick){
        BukkitTask createdTask = Bukkit.getScheduler().runTaskTimerAsynchronously(SInventoryInstance.pluginHook, task, delayTick, intervalTick);
        bukkitTasks.add(createdTask);
        return createdTask;
    }


    public final BukkitTask runTaskLater(Runnable task, int delayTick){
        BukkitTask createdTask = Bukkit.getScheduler().runTaskLater(SInventoryInstance.pluginHook, task, delayTick);
        bukkitTasks.add(createdTask);
        return createdTask;
    }

    public final BukkitTask runTaskLaterAsync(Runnable task, int delayTick){
        BukkitTask createdTask = Bukkit.getScheduler().runTaskLaterAsynchronously(SInventoryInstance.pluginHook, task, delayTick);
        bukkitTasks.add(createdTask);
        return createdTask;
    }

    public final BukkitTask runTask(Runnable task){
        BukkitTask createdTask = Bukkit.getScheduler().runTask(SInventoryInstance.pluginHook, task);
        bukkitTasks.add(createdTask);
        return createdTask;
    }

    public final BukkitTask runTaskAsync(Runnable task){
        BukkitTask createdTask = Bukkit.getScheduler().runTaskAsynchronously(SInventoryInstance.pluginHook, task);
        bukkitTasks.add(createdTask);
        return createdTask;
    }

    public static void cancelAllBukkitTasks(SInventoryObject startingPoint){
        for(BukkitTask task: startingPoint.bukkitTasks){
            task.cancel();
        }
        for(SInventoryObject obj: startingPoint.childObjects){
            cancelAllBukkitTasks(obj);
        }
    }


}
