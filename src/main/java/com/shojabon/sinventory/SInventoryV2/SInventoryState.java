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

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void addOnClickEvent(Consumer<T> event){
        this.onSetEvents.add(event);
    }
}
