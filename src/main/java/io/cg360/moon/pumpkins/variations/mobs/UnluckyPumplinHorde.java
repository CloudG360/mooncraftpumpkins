package io.cg360.moon.pumpkins.variations.mobs;

import io.cg360.moon.pumpkins.Utils;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
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

public class UnluckyPumplinHorde extends PumpkinLuckyRoll {

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){
        int luck = Math.max(1, luckmult);
        int qty = 5 + (new Random().nextInt(10 * (1/luck)));

        createHorde(loc, qty);

        player.sendMessage(Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "TRICK", TextStyles.RESET, TextColors.GOLD, " You have been visited by Spooky llamas."));
    }

    private static void createHorde(Location<World> loc, int quantity){
        ItemStack pk = ItemStack.builder().itemType(ItemTypes.PUMPKIN).quantity(1).build();
        Optional<EnchantmentData> dat = pk.getOrCreate(EnchantmentData.class);
        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(dat.get().enchantments().add(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1)));
            pk.offer(data);
        }
        pk.offer(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.DARK_GRAY, "Cursed ", TextColors.GOLD, "Pumpkin"));
        pk.offer(Keys.ITEM_LORE, Arrays.asList(
                Text.of(TextColors.BLACK, "lucky_pumplin"),
                Text.of(TextColors.RED, "You should not have this. It's a bug.")
        ));
        ItemStackSnapshot helm = pk.createSnapshot();

        for(int i = 0; i < quantity; i++){
            int xoffset = new Random().nextInt(11) - 5;
            int zoffset = new Random().nextInt(11) - 5;
            Location<World> nloc = new Location<>(loc.getExtent(), loc.getX()+xoffset, loc.getExtent().getHighestYAt((int) Math.floor(loc.getX()+xoffset), (int) Math.floor(loc.getZ()+zoffset)) + 1, loc.getZ()+zoffset);
            spawnHordeZombie(nloc, helm);
        }
    }

    private static Entity spawnHordeZombie(Location<World> loc, ItemStackSnapshot pumpkinhelmet){
        Entity e = loc.getExtent().createEntity(EntityTypes.ZOMBIE, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(Utils.parseToSpongeString("&6&lPumplin &8&lMinion")));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        Zombie z = (Zombie) e;

        z.setHelmet(pumpkinhelmet.createStack());
        z.offer(Keys.IS_ADULT, false);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(z);
        }
        return z;
    }
}
