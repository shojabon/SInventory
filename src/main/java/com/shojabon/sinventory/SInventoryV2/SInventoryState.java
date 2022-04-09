package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SInventoryState <T>{

    T value;


    private ArrayList<Consumer<T>> onSetEvents = new ArrayList<>();

    public SInventoryState(T defaultValue) {
        this.value = defaultValue;
    }

    public SInventoryState(){
        this.value = null;
    }

    public void set(T value) {
        this.value = value;
        for(Consumer<T> consumer: onSetEvents){
            consumer.accept(value);
        }
    }

    public T get() {
        return value;
    }

    public void addOnSetEvent(Consumer<T> event){
        this.onSetEvents.add(event);
    }
}
