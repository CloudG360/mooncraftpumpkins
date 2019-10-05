package io.cg360.moon.pumpkins.variations.mobs;

import io.cg360.moon.pumpkins.Utils;
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

public class LuckySpookyLlamas extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        int height = (3 + new Random().nextInt(3 + 3* luckmult));
        spawnLlamaStack(loc, height);
        spawnLlamaStack(loc, height);
        spawnLlamaStack(loc, height);
        spawnLlamaStack(loc, height);
        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT", TextStyles.RESET, "You have been visited by Spooky llamas."));
    }

    private static void spawnLlamaStack(Location<World> loc, int height){
        int xoffset = new Random().nextInt(11) - 5;
        int zoffset = new Random().nextInt(11) - 5;

        Location<World> nloc = new Location<>(loc.getExtent(), loc.getX()+xoffset, loc.getExtent().getHighestYAt((int) Math.floor(loc.getX()+xoffset), (int) Math.floor(loc.getZ()+zoffset)) + 1, loc.getZ()+zoffset);

        Entity last = stackLlama(nloc, null);
        for(int i = 0; i < height; i++){
            last = stackLlama(nloc, last);
        }
    }

    private static Entity stackLlama(Location<World> loc, Entity parent){
        Entity e = loc.getExtent().createEntity(EntityTypes.LLAMA, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(Utils.parseToSpongeString("&7&lFeel the &6&lSPOOK")));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        if(parent != null) e.setVehicle(parent);
        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }
}
