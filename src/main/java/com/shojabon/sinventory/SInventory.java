package com.shojabon.sinventory;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.SInventoryV2.SInventoryObject;
import com.shojabon.sinventory.SInventoryV2.SInventoryPosition;
import com.shojabon.sinventory.SInventoryV2.modules.SInvCounter;
import com.shojabon.sinventory.SInventoryV2.modules.SInvTestModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SInventory extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        SInventoryInstance.pluginHook = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;

        SInvTestModule counter = new SInvTestModule();
        counter.open(p);

        return false;
    }
}
