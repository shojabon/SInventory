package com.shojabon.sinventory.demos;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryState;
import com.shojabon.sinventory.SInventoryV2.VRender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RainbowMenu extends SInventoryInstance {

    public SInventoryState<Integer> randomSeed = new SInventoryState<>(1);
    Material[] materials = new Material[]{
            Material.BLUE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
    };

    @Override
    public void onMount() {
        runTaskTimerAsync(()->{
            randomSeed.set(new Random().nextInt());
        }, 0, 0);
    }

    @Override
    public VRender render(VRender render) {
        Random r = new Random(randomSeed.get());
        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 6; y++){
                render.set(x, y, new ItemStack(materials[r.nextInt(materials.length)]));
            }
        }
        return render;
    }
}
