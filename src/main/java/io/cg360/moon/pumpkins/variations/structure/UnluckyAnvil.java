package io.cg360.moon.pumpkins.variations.structure;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class UnluckyAnvil extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY() + 10;
        int z = player.getLocation().getBlockZ();

        new Location<>(loc.getExtent(), x, y, z).setBlockType(BlockTypes.ANVIL);
        new Location<>(loc.getExtent(), x, y-1, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-2, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-3, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-4, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-5, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-6, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-7, z).setBlockType(BlockTypes.AIR);
        new Location<>(loc.getExtent(), x, y-8, z).setBlockType(BlockTypes.AIR);

        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TRICK ", TextStyles.RESET, TextColors.GOLD, "ACME Inc delivery incoming."));
    }
}
