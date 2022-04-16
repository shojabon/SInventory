package com.shojabon.sinventory.demos;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryPosition;
import com.shojabon.sinventory.SInventoryV2.SInventoryState;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Timer;
import java.util.Vector;

public class Captcha extends SInventoryInstance {

    public SInventoryState<Integer> directionX = new SInventoryState<>(1);
    public SInventoryState<Integer> directionY = new SInventoryState<>(1);

    public SInventoryState<Integer> locationX = new SInventoryState<>(1);
    public SInventoryState<Integer> locationY = new SInventoryState<>(1);

    public Captcha(){
        setTitle("§l§aCaptcha 赤をクリックしてください");
        setClickable(false);

        runTaskTimerAsync(()->{
            int nextX = locationX.get() + directionX.get();
            int nextY = locationY.get() + directionY.get();
            int directX = directionX.get();
            int directY = directionY.get();
            if(nextX < 0 || nextX > 8) {
                directX *= -1;
            }
            if(nextY < 0 || nextY > rows-1) {
                directY *= -1;
            }
            nextX = locationX.get() + directX;
            nextY = locationY.get() + directY;
            directionX.setNoExecuteEvent(directX);
            directionY.setNoExecuteEvent(directY);
            locationX.setNoExecuteEvent(nextX);
            locationY.setNoExecuteEvent(nextY);
            invokeRender();
        }, 1, 1);
    }

    @Override
    public void onClick(InventoryClickEvent event, SInventoryPosition relativePosition) {
        Bukkit.broadcastMessage("click!");
    }

    @Override
    public VRender render(VRender render) {
        render.set(locationX.get(), locationY.get(), new ItemStack(Material.RED_STAINED_GLASS_PANE));
        return render;
    }
}
