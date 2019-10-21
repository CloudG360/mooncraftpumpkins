package io.cg360.moon.pumpkins.variations.item.charms;

import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;

public class LuckyCharmHercules extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.BLAZE_POWDER).quantity(1).build();
        itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "Hercules Charm"));
        itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                    Text.of(TextColors.BLACK, "strength_charm"),
                    Text.of(TextColors.RED, "Gives you Strength 3"),
                    Text.of(TextColors.RED, "when held in the off hand.")
                ));

        Optional<EnchantmentData> dat = itemStack.getOrCreate(EnchantmentData.class);
        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(dat.get().enchantments().add(Enchantment.of(EnchantmentTypes.PROTECTION, 1)));
            itemStack.offer(data);
        }

        ItemStackSnapshot s = itemStack.createSnapshot();
        Entity e = player.getWorld().createEntity(EntityTypes.ITEM, loc.getPosition().add(0, 0.25, 0));
        e.offer(Keys.REPRESENTED_ITEM, s);
        loc.getExtent().spawnEntity(e);

        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT", TextStyles.RESET, TextColors.GOLD, " You got a Hercules Charm!"));
    }
}
