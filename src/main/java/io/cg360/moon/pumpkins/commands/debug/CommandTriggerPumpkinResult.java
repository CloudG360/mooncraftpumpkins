package io.cg360.moon.pumpkins.commands.debug;

import io.cg360.moon.pumpkins.PumpkinsPlugin;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CommandTriggerPumpkinResult implements CommandExecutor {


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Oi, you're not a player..."));
            return CommandResult.success();
        }

        Optional<String> id = args.getOne(Text.of("id"));
        if(!id.isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "No id specified"));
            return CommandResult.success();
        }
        String idstr = id.get();

        PumpkinsPlugin.getPumpkinsPlugin().getLogger().info(PumpkinsPlugin.getPumpkinsPlugin().getPumpkinResultManager().getPumpkinRollMap().toString());

        Optional<PumpkinLuckyRoll> rl = PumpkinsPlugin.getPumpkinsPlugin().getPumpkinResultManager().getRoll(idstr);
        if(!rl.isPresent()){
            src.sendMessage(Text.of(TextColors.RED, "Invalid id! Broken!"));
            return CommandResult.success();
        }
        rl.get().execute((Player) src, ((Player) src).getLocation(), 0);

        return CommandResult.success();
    }
}
