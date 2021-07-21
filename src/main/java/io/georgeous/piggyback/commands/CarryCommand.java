package io.georgeous.piggyback.commands;

import io.georgeous.piggyback.Piggyback;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CarryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(Piggyback.carryCoupleMap.containsKey(sender)){
            Piggyback.carryCoupleMap.get(sender).toggleMode();
        }


        return false;
    }
}