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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

public class UnluckyGhastTrio extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){

        createGhast(loc, "Boo");
        createGhast(loc, "Hoo");
        createGhast(loc, "Moo?");

        player.sendMessage(Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "TRICK", TextStyles.RESET, TextColors.GOLD, " Ghosts. Just some ghosts. Only ghosts."));
    }

    private static void createGhast(Location<World> loc, String name){

        int xoffset = new Random().nextInt(4) - 3;
        int zoffset = new Random().nextInt(4) - 3;
        Location<World> nloc = new Location<>(loc.getExtent(), loc.getX()+xoffset, loc.getExtent().getHighestYAt((int) Math.floor(loc.getX()+xoffset), (int) Math.floor(loc.getZ()+zoffset)) + 1, loc.getZ()+zoffset);
        spawnG(nloc, name);

    }

    private static Entity spawnG(Location<World> loc, String name){
        Entity e = loc.getExtent().createEntity(EntityTypes.GHAST, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, name));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }
}
