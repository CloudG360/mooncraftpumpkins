package io.cg360.moon.pumpkins.variations.mobs;

import io.cg360.moon.pumpkins.PumpkinsPlugin;
import io.cg360.moon.pumpkins.Utils;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.monster.WitherSkeleton;
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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class UnluckyPumpking extends PumpkinLuckyRoll {

    public static final int RADIUS = 60;

    @Override
    protected void run(Player player, Location<World> loc, int luckmult){

        Entity e = loc.getExtent().createEntity(EntityTypes.WITHER_SKELETON, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "The Pumpkin King"));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        WitherSkeleton w = (WitherSkeleton) e;

        ItemStack helm = ItemStack.builder().itemType(ItemTypes.LIT_PUMPKIN).build();
        helm.getOrCreate(EnchantmentData.class).ifPresent(enchantmentData -> {
            EnchantmentData d = enchantmentData.set(enchantmentData.enchantments().add(Enchantment.of(EnchantmentTypes.PROTECTION, 10)));
            helm.offer(d);
        });
        helm.offer(Keys.ITEM_LORE, Arrays.asList(Text.of(TextColors.BLACK, "boss_pumpking"), Text.of(TextColors.RED, "You should not have this. It's a bug.")));
        ItemStack chest = ItemStack.builder().itemType(ItemTypes.LIT_PUMPKIN).build();
        chest.getOrCreate(EnchantmentData.class).ifPresent(enchantmentData -> {
            EnchantmentData d = enchantmentData.set(enchantmentData.enchantments()
                    .add(Enchantment.of(EnchantmentTypes.PROTECTION, 5))
                    .add(Enchantment.of(EnchantmentTypes.THORNS, 3))
                    .add(Enchantment.of(EnchantmentTypes.UNBREAKING, 3))
                    .add(Enchantment.of(EnchantmentTypes.BLAST_PROTECTION, 100))
            );
            chest.offer(d);
        });
        ItemStack leg = ItemStack.builder().itemType(ItemTypes.LIT_PUMPKIN).build();
        leg.getOrCreate(EnchantmentData.class).ifPresent(enchantmentData -> {
            EnchantmentData d = enchantmentData.set(enchantmentData.enchantments()
                    .add(Enchantment.of(EnchantmentTypes.PROTECTION, 5))
                    .add(Enchantment.of(EnchantmentTypes.UNBREAKING, 3))
            );
            leg.offer(d);
        });
        ItemStack foot = ItemStack.builder().itemType(ItemTypes.DIAMOND_BOOTS).build();
        foot.getOrCreate(EnchantmentData.class).ifPresent(enchantmentData -> {
            EnchantmentData d = enchantmentData.set(enchantmentData.enchantments()
                    .add(Enchantment.of(EnchantmentTypes.PROTECTION, 5))
                    .add(Enchantment.of(EnchantmentTypes.FROST_WALKER, 4))
                    .add(Enchantment.of(EnchantmentTypes.FEATHER_FALLING, 3))
                    .add(Enchantment.of(EnchantmentTypes.UNBREAKING, 3))
            );
            foot.offer(d);
        });

        w.setHelmet(helm);
        w.setChestplate(chest);
        w.setLeggings(leg);
        w.setBoots(foot);

        w.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> {
            PotionEffectData d = potionEffectData.set(potionEffectData.effects()
                    .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 3, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.FIRE_RESISTANCE, 1, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.GLOWING, 1, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.HEALTH_BOOST, 10, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.GLOWING, 1, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.STRENGTH, 3, 100000))
                    .add(PotionEffect.of(PotionEffectTypes.SPEED, 2, 100000))
            );
        });

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(w);
            ServerBossBar bb = ServerBossBar.builder()
                    .color(BossBarColors.PURPLE)
                    .createFog(true)
                    .darkenSky(true)
                    .name(Text.of(TextColors.GOLD, TextStyles.BOLD, "The Pumpkin King"))
                    .overlay(BossBarOverlays.NOTCHED_10)
                    .build();
            for(Player pl:loc.getExtent().getPlayers()){
                if(loc.getPosition().distance(pl.getPosition()) <= RADIUS){
                    pl.sendMessage(Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "TRICK", TextStyles.RESET, TextColors.GOLD, " The Pumpkin King has arrived..."));
                    pl.playSound(SoundTypes.ENTITY_WITHER_AMBIENT, pl.getPosition(), 1d, 0.5d);
                    bb.addPlayer(pl);
                }
            }

            Task bossbar = Task.builder()
                    .execute(task -> {
                        if(w.isRemoved()){
                            bb.setVisible(false);
                            task.cancel();
                            return;
                        }
                        bb.setPercent((float) (w.health().get() / w.maxHealth().get()));
                    })
                    .intervalTicks(10)
                    .name(w.getUniqueId().toString())
                    .submit(PumpkinsPlugin.getPumpkinsPlugin());
            Task behaviors = Task.builder()
                    .execute(task -> {
                        if(w.isRemoved()){
                            task.cancel();
                            return;
                        }
                        int t = new Random().nextInt(2);
                        if(t == 0) {
                            Entity tnt = w.getLocation().getExtent().createEntity(EntityTypes.PRIMED_TNT, w.getLocation().getPosition());
                            PrimedTNT tnte = (PrimedTNT) tnt;

                            try (CauseStackManager.StackFrame fr = Sponge.getCauseStackManager().pushCauseFrame()) {
                                fr.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLACEMENT);
                                loc.getExtent().spawnEntity(tnte);
                            }
                        }
                    })
                    .intervalTicks(40)
                    .name(w.getUniqueId().toString())
                    .submit(PumpkinsPlugin.getPumpkinsPlugin());
        }

        createHorde(loc, 10);
    }

    private static void createHorde(Location<World> loc, int quantity){
        ItemStack pk = ItemStack.builder().itemType(ItemTypes.PUMPKIN).quantity(1).build();
        Optional<EnchantmentData> dat = pk.getOrCreate(EnchantmentData.class);
        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(dat.get().enchantments().add(Enchantment.of(EnchantmentTypes.BLAST_PROTECTION, 10)).add(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1)));
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
