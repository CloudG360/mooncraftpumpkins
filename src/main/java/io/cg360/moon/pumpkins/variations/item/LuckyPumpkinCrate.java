package io.cg360.moon.pumpkins.variations.item;

import io.cg360.moon.pumpkins.PumpkinConfiguration;
import io.cg360.moon.pumpkins.PumpkinsPlugin;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import io.cg360.moon.supplykeys.entities.items.SerializableItem;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class LuckyPumpkinCrate extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        Optional<SerializableItem> i = PumpkinsPlugin.getSKPlugin().getPoolManager().getItem(PumpkinConfiguration.get().getPumpkinCrateKeyID());
        ItemStack itemStack;
        if(i.isPresent()){
            Random r = new Random();
            int amount = 1 + r.nextInt(1 + luckmult);
            itemStack = i.get().generateStack(amount);
        } else {
            itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).quantity(1).build();
            itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Spooky I.O.U"));
            itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                        Text.of(TextColors.BLACK, "IOU"),
                        Text.of(TextColors.RED, String.format("You are owed a '%s' but it appears the key doesn't exist. Dev error.", PumpkinConfiguration.get().getPumpkinCrateKeyID())),
                        Text.of(TextColors.BLACK, PumpkinConfiguration.get().getPumpkinCrateKeyID())
                    ));

        }

        ItemStackSnapshot s = itemStack.createSnapshot();
        Entity e = player.getWorld().createEntity(EntityTypes.ITEM, loc.getPosition().add(0, 0.25, 0));
        e.offer(Keys.REPRESENTED_ITEM, s);
        loc.getExtent().spawnEntity(e);



        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT", TextStyles.RESET, TextColors.GOLD, "You got a pumpkin key!"));
    }
}
