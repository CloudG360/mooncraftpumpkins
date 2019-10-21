package io.cg360.moon.pumpkins.variations.mobs;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LuckyPinkSheep extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){

        spawnPinkSheep(loc);

        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT", TextStyles.RESET, TextColors.GOLD, " A pink sheep?"));
    }

    private static Entity spawnPinkSheep(Location<World> loc){
        Entity e = loc.getExtent().createEntity(EntityTypes.SHEEP, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD, "Pink Sheep?"));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        e.offer(Keys.COLOR, Color.PINK);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }
}
