package io.cg360.moon.pumpkins.variations.structure;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class UnluckyFire extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        new Location<>(loc.getExtent(), x, y, z).setBlockType(BlockTypes.FIRE);
        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TRICK ", TextStyles.RESET, TextColors.GOLD, "Burn baby, burn."));
    }
}
