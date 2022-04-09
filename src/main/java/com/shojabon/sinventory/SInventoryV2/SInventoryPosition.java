package com.shojabon.sinventory.SInventoryV2;

import org.bukkit.Bukkit;

import java.util.Objects;

public class SInventoryPosition{

    public int x = 0;
    public int y = 0;
    public int z = 0;

    private boolean twoDimensional;


    public SInventoryPosition(int x, int y){
        this.x = x;
        this.y = y;
        twoDimensional = true;
    }

    public SInventoryPosition(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        twoDimensional = false;
    }


    public String get2DString(){
        return this.x + "-" + this.y;
    }

    public String get3DString(){
        return this.x + "-" + this.y + "-" + this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SInventoryPosition that = (SInventoryPosition) o;
        return x == that.x && y == that.y && z == that.z && twoDimensional == that.twoDimensional;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, twoDimensional);
    }
}
