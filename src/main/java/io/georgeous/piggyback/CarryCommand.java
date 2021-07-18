package io.georgeous.piggyback;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CarryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(Piggyback.passengerMode){
            Piggyback.passengerMode = false;
        }else{
            Piggyback.passengerMode = true;
        }
        return false;
    }
}
