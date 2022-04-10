package com.shojabon.sinventory;

import com.shojabon.sinventory.SInventoryV2.SInventoryInstance;
import com.shojabon.sinventory.demos.Captcha;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SInventory extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        SInventoryInstance.onEnableHook(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SInventoryInstance.onDisableHook();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;

//        RainbowMenu menu = new RainbowMenu();

        Captcha menu = new Captcha();
        menu.open(p);

        return false;
    }
}
