package io.cg360.moon.pumpkins.variations.item;

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

public class UnluckyPumpkinHelm extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        Optional<ItemStack> hstack = player.getHelmet();
        if(hstack.isPresent()) {
            ItemStackSnapshot oldhelmet = hstack.get().createSnapshot();
            player.setHelmet(ItemStack.builder().itemType(ItemTypes.AIR).build());
            Entity e = player.getWorld().createEntity(EntityTypes.ITEM, player.getPosition().add(0, 1, 0));
            e.offer(Keys.REPRESENTED_ITEM, oldhelmet);
            player.getWorld().spawnEntity(e);
        }

        ItemStack pk = ItemStack.builder().itemType(ItemTypes.PUMPKIN).quantity(1).build();
        Optional<EnchantmentData> dat = pk.getOrCreate(EnchantmentData.class);

        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(
                    dat.get().enchantments()
                    .add(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1))
                    .add(Enchantment.of(EnchantmentTypes.PROTECTION, 1+luckmult)) // buying more luck causes punishments to be nicer.
            );
            pk.offer(data);
        }

        pk.offer(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.DARK_GRAY, "Cursed ", TextColors.GOLD, "Pumpkin"));
        pk.offer(Keys.ITEM_LORE, Arrays.asList(
                Text.of(TextColors.GOLD, "Legends say curses can"),
                Text.of(TextColors.GOLD, "only be ", TextColors.RED, TextStyles.BOLD, "BURNED", TextStyles.BOLD, TextColors.GOLD, "off...")
        ));
        player.setHelmet(pk);

        player.sendMessage(Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "TRICK", TextStyles.RESET, TextColors.GOLD, "Forever shall you view the world through the pumpkin's eyes."));
        player.sendMessage(Text.of(TextColors.GOLD, "Legends say binding curses can only be ", TextColors.RED, TextStyles.BOLD, "BURNED", TextStyles.BOLD, TextColors.GOLD, "off..."));
    }
}
