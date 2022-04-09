package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class VRender {

    SInventoryObject invObj;
    public VRender(SInventoryObject obj){
        this.invObj = obj;
    }

    public HashMap<SInventoryPosition, ItemStack> iRender = new HashMap<>();
    public HashMap<SInventoryPosition, SInventoryObject> clickEventHandling = new HashMap<>();

    public void set(int x, int y, ItemStack item){
        iRender.put(new SInventoryPosition(x + invObj.objectLocation.x, y + invObj.objectLocation.y), item);
        clickEventHandling.put(new SInventoryPosition(x + invObj.objectLocation.x, y + invObj.objectLocation.y), this.invObj);
    }

    public void mergeRender(VRender render){
        for(SInventoryPosition pos: render.iRender.keySet()){
            SInventoryPosition offsetAppliedPos = new SInventoryPosition(pos.x, pos.y);
            if(iRender.containsKey(offsetAppliedPos)){
                if(invObj.objectLocation.z > render.invObj.objectLocation.z){
                    continue;
                }
            }
            iRender.put(offsetAppliedPos, render.iRender.get(pos));
            clickEventHandling.put(offsetAppliedPos, render.invObj);
        }
    }

    public VRender copy(){
        VRender result = new VRender(this.invObj);
        result.iRender = iRender;
        result.clickEventHandling = clickEventHandling;

        return result;
    }

}
