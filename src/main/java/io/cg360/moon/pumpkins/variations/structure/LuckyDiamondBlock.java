package io.cg360.moon.pumpkins.variations.structure;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LuckyDiamondBlock extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        loc.setBlockType(BlockTypes.DIAMOND_BLOCK);
        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT ", TextStyles.RESET, TextColors.GOLD, "You got a diamond block!"));
    }
}
