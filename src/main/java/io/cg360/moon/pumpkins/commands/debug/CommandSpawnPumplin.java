package io.cg360.moon.pumpkins.commands.debug;

import io.cg360.moon.pumpkins.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandSpawnPumplin implements CommandExecutor {


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Oi, you're not a player..."));
            return CommandResult.success();
        }

        Utils.spawnPumplinZombie(((Player) src).getLocation());

        return CommandResult.success();
    }
}
