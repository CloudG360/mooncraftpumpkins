package io.cg360.moon.pumpkins.variations.mobs;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.OcelotTypes;
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

import java.util.Optional;

public class UnluckyWitch extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){

        spawnWitch(loc, "Margret");
        spawnWitch(loc, "Debra");
        spawnWitch(loc, "Frog's left toe");
        spawnWitch(loc, "bertha.");
        spawnCat(player, loc, "Simon");

        player.sendMessage(Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "TRICK", TextStyles.RESET, TextColors.GOLD, " Uh oh, a coven of witches!"));
    }

    private static Entity spawnWitch(Location<World> loc, String name){
        Entity e = loc.getExtent().createEntity(EntityTypes.WITCH, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD, name));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }

    private static Entity spawnCat(Player player, Location<World> loc, String name){
        Entity e = loc.getExtent().createEntity(EntityTypes.OCELOT, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, name));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        e.offer(Keys.TAMED_OWNER, Optional.of(player.getUniqueId()));
        e.offer(Keys.OCELOT_TYPE, OcelotTypes.BLACK_CAT);


        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }
}
