package io.cg360.moon.pumpkins.variations.item.wands;

import io.cg360.moon.pumpkins.PumpkinConfiguration;
import io.cg360.moon.pumpkins.PumpkinsPlugin;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import io.cg360.moon.supplykeys.entities.items.SerializableItem;
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
import java.util.Random;

public class LuckyScreamingStick extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        Optional<SerializableItem> i = PumpkinsPlugin.getSKPlugin().getPoolManager().getItem(PumpkinConfiguration.get().getPumpkinCrateKeyID());
        ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.STICK).quantity(1).build();

        int offluck = luckmult + 1;
        int lucklvl = new Random().nextInt((2 + offluck) * 3) + 5;

        itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD, "Wand of Screaming"));
        itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                    Text.of(TextColors.BLACK, String.format("cooldown:6,screaming:%s", lucklvl)),
                    Text.of(TextColors.YELLOW, "What else is there to know?"),
                    Text.of(TextColors.YELLOW, "It screams at your enemies."),
                    Text.of(TextColors.YELLOW, "(but makes them quicker)"),
                    Text.of(""),
                    Text.of(TextColors.RED, TextStyles.BOLD, "Cooldown: 6s"),
                    Text.of(TextColors.GOLD, TextStyles.BOLD, String.format("Radius: %s", lucklvl))
                ));

        Optional<EnchantmentData> dat = itemStack.getOrCreate(EnchantmentData.class);
        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(dat.get().enchantments().add(Enchantment.of(EnchantmentTypes.KNOCKBACK, 10)));
            itemStack.offer(data);
        }

        ItemStackSnapshot s = itemStack.createSnapshot();
        Entity e = player.getWorld().createEntity(EntityTypes.ITEM, loc.getPosition().add(0, 0.25, 0));
        e.offer(Keys.REPRESENTED_ITEM, s);
        loc.getExtent().spawnEntity(e);

        player.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "TREAT", TextStyles.RESET, TextColors.GOLD, " You got a wand!"));
    }
}
