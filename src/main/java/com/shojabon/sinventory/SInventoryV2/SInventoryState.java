package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SInventoryState <T>{

    T value;


    private ArrayList<Consumer<T>> onSetEvents = new ArrayList<>();
    Consumer<T> onSetRenderEvent = null;

    public SInventoryState(T defaultValue) {
        this.value = defaultValue;
    }

    public SInventoryState(){
        this.value = null;
    }

    public void set(T value) {
        this.value = value;
        if(onSetRenderEvent != null) onSetRenderEvent.accept(value);
        for(Consumer<T> consumer: onSetEvents){
            try{consumer.accept(value);}catch (Exception exception){exception.printStackTrace();}
        }
    }

    public void setNoExecuteEvent(T value){
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void addOnSetEvent(Consumer<T> event){
        this.onSetEvents.add(event);
    }
}
