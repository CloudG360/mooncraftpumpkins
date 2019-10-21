package io.cg360.moon.pumpkins;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import io.cg360.moon.pumpkins.charms.CharmTask;
import io.cg360.moon.pumpkins.commands.debug.CommandSpawnPumplin;
import io.cg360.moon.pumpkins.commands.debug.CommandTriggerPumpkinResult;
import io.cg360.moon.pumpkins.managers.PumpkinResultManager;
import io.cg360.moon.pumpkins.variations.PumpkinType;
import io.cg360.moon.pumpkins.variations.item.LuckyPumpkinCrate;
import io.cg360.moon.pumpkins.variations.item.UnluckyPumpkinHelm;
import io.cg360.moon.pumpkins.variations.item.charms.*;
import io.cg360.moon.pumpkins.variations.item.wands.LuckyDamagingStick;
import io.cg360.moon.pumpkins.variations.item.wands.LuckyLeapingStick;
import io.cg360.moon.pumpkins.variations.item.wands.LuckyScreamingStick;
import io.cg360.moon.pumpkins.variations.item.wands.LuckySlothStick;
import io.cg360.moon.pumpkins.variations.mobs.*;
import io.cg360.moon.pumpkins.variations.structure.*;
import io.cg360.moon.supplykeys.Supplykeys;
import io.cg360.moon.supplykeys.exceptions.MalformedLootPoolException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.WitherSkeleton;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.BlockChangeFlags;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Plugin(
        id = "pumpkins",
        name = "Pumpkins",
        version = "1.0-spookshot",
        description = "Handles Pumpkin lucky blocks.",
        authors = {
                "CloudGamer360"
        },
        dependencies = {
                @Dependency(id = "supplykeys", optional = false)
        }
)
public class PumpkinsPlugin {

    private static PumpkinsPlugin pumpkinsPlugin;
    private static Supplykeys supplykeys;

    private PumpkinResultManager pumpkinResultManager;

    private PumpkinConfiguration configuration;

    private HashMap<ItemStackSnapshot, LocalDateTime> cooldowns;
    private ArrayList<UUID> ignoreIDs;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        PluginContainer p = Sponge.getPluginManager().getPlugin("supplykeys").orElse(null);

        supplykeys = (Supplykeys) p.getInstance().orElse(null);
        pumpkinsPlugin = this;

        cooldowns = new HashMap<>();
        ignoreIDs = new ArrayList<>();

        pumpkinResultManager = new PumpkinResultManager();

        loadConfig();

        // -------

        pumpkinResultManager.registerRoll(new LuckySpookyLlamas().setConfig("m_llamas", 15, PumpkinType.MIXED));



        pumpkinResultManager.registerRoll(new LuckyPumpkinCrate().setConfig("l_pumpkin_crate", 10, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyScreamingStick().setConfig("l_wand_screaming", 5, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyLeapingStick().setConfig("l_wand_leaping", 5, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyDamagingStick().setConfig("l_wand_damaging", 5, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckySlothStick().setConfig("l_wand_slowing", 5, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyPinkSheep().setConfig("l_mob_sheeppink", 10, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyDiamondBlock().setConfig("l_block_diamond", 10, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyEmeraldBlock().setConfig("l_block_emerald", 10, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyDragonEgg().setConfig("l_block_egg", 10, PumpkinType.LUCKY));

        pumpkinResultManager.registerRoll(new LuckyCharmBunny().setConfig("l_charm_bunny", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmFloating().setConfig("l_charm_floating", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmGills().setConfig("l_charm_gills", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmHealing().setConfig("l_charm_healing", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmHercules().setConfig("l_charm_hercules", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmInvisibility().setConfig("l_charm_invis", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmLava().setConfig("l_charm_lava", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmLife().setConfig("l_charm_life", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmMining().setConfig("l_charm_mining", 3, PumpkinType.LUCKY));
        pumpkinResultManager.registerRoll(new LuckyCharmSwiftness().setConfig("l_charm_swiftness", 3, PumpkinType.LUCKY));


        pumpkinResultManager.registerRoll(new UnluckyPumpkinHelm().setConfig("u_cursed_helm", 10, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyPumplinHorde().setConfig("u_pumplin_raid", 20, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyPumpking().setConfig("u_pumpking", 5, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyGhastTrio().setConfig("u_three_ghosts", 15, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyGhastTrio().setConfig("u_witches", 15, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyAnvil().setConfig("u_anvil", 15, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyFire().setConfig("u_fire", 15, PumpkinType.UNLUCKY));
        pumpkinResultManager.registerRoll(new UnluckyLava().setConfig("u_lava", 15, PumpkinType.UNLUCKY));

        // -------

        CommandSpec debugpumpkintest = CommandSpec.builder()
                .description(Text.of("Debug command for testing lucky pumpkin loot."))
                .permission("pumpkins.debug.testrolls")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.choices(Text.of("id"), pumpkinResultManager.getAutoCompleteMap()))
                )
                .executor(new CommandTriggerPumpkinResult())
                .build();
        CommandSpec debugpumplin = CommandSpec.builder()
                .description(Text.of("Spawns a pumplin"))
                .permission("pumpkins.debug.pumplins")
                .executor(new CommandSpawnPumplin())
                .build();

        Sponge.getCommandManager().register(this, debugpumpkintest, "debugpumpkintest");
        Sponge.getCommandManager().register(this, debugpumplin, "debugspawnpumplin");

        // -------

        Task t = Task.builder().execute(new CharmTask()).async().intervalTicks(10).submit(this);

    }

    @Listener
    public void onDismount(RideEntityEvent.Dismount event){
        if(!event.getCause().containsType(Player.class)){
            event.setCancelled(true);
        }
    }

    @Listener
    public void onDamage(DamageEntityEvent event){
        Optional<Entity> e = event.getCause().first(Entity.class);
        if(!e.isPresent()) return;
        if(this.ignoreIDs.contains(e.get().getUniqueId())){
            event.setCancelled(true);
            ignoreIDs.remove(e.get().getUniqueId());
        }
    }

    @Listener
    public void onItemUse(InteractItemEvent event){
        // -------------------------
        if(event.getItemStack().get(Keys.ITEM_LORE).isPresent()){
            List<Text> t = event.getItemStack().get(Keys.ITEM_LORE).get();
            if(t.size() > 0){
                String[] tags = t.get(0).toPlain().split(Pattern.quote(","));
                if(tags.length > 0) {

                    // ------------------

                    Optional<String> coolCheck = Utils.tagStartsWith(tags, "cooldown");
                    if (coolCheck.isPresent()) {
                        Optional<Player> p = event.getCause().first(Player.class);
                        if (p.isPresent()) {
                            LocalDateTime now = LocalDateTime.now();
                            Player player = p.get();
                            if (cooldowns.containsKey(event.getItemStack())) {
                                LocalDateTime dt = cooldowns.get(event.getItemStack());
                                if (dt.isBefore(now)) {
                                    addCooldown(coolCheck.get(), event.getItemStack());
                                } else {
                                    String ctime = Utils.compareDateTimes(now, dt);
                                    event.setCancelled(true);
                                    player.sendTitle(Title.builder()
                                            .actionBar(Text.of(Text.of(TextColors.DARK_RED, TextStyles.BOLD, "COOLDOWN", TextStyles.RESET, TextColors.RED, " You have to wait " + ctime + " before using this item.")))
                                            .fadeIn(3)
                                            .stay(30)
                                            .build()
                                    );
                                    player.resetTitle();
                                    return;
                                }
                            } else {
                                addCooldown(coolCheck.get(), event.getItemStack());
                            }
                        }
                    }

                    // ------------------

                    Optional<String> screamingCheck = Utils.tagStartsWith(tags, "screaming");
                    if (screamingCheck.isPresent()) {
                        String le = screamingCheck.get();
                        Optional<Player> p = event.getCause().first(Player.class);
                        if (p.isPresent()) {
                            int rad = 5;
                            String[] sp = le.split(":");
                            if(sp.length > 1) try { rad = Integer.parseInt(sp[1]); } catch (NumberFormatException ignored){ }
                            Player player = p.get();
                            int o = new Random().nextInt(3);
                            switch (o) {
                                case 0: player.getWorld().playSound(SoundTypes.ENTITY_GHAST_SCREAM, player.getPosition(), 1f); break;
                                case 1: player.getWorld().playSound(SoundTypes.ENTITY_GHAST_WARN, player.getPosition(), 1f); break;
                                case 2: player.getWorld().playSound(SoundTypes.ENTITY_GHAST_HURT, player.getPosition(), 1f); break;
                            }
                            player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.HUGE_EXPLOSION).build(), player.getPosition().add(0, 0.25, 0));
                            player.getNearbyEntities(rad).forEach(e -> {
                                if(e.getUniqueId() != player.getUniqueId()) {
                                    if(!e.getType().getId().startsWith("fossils")) {
                                        Vector3d dp = player.getLocation().getPosition().sub(e.getLocation().getPosition());
                                        Vector3d d2 = dp.negate().add(0,dp.getY() * 2, 0).normalize();
                                        player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.WATER_SPLASH).quantity(15).build(), e.getLocation().getPosition().add(0, 1.5, 0));
                                        Optional<PotionEffectData> d = e.getOrCreate(PotionEffectData.class);
                                        if(d.isPresent()){
                                            PotionEffectData data = d.get();
                                            int speed = 2;
                                            final int ti = 20 * 60;
                                            for(PotionEffect effect:data.getListValue().copy()){
                                                if(effect.getType() == PotionEffectTypes.SPEED) {
                                                    speed += effect.getAmplifier();
                                                    data.remove(effect);
                                                    break;
                                                }
                                            }
                                            final int ef = speed;
                                            data.addElement(PotionEffect.builder().potionType(PotionEffectTypes.SPEED).amplifier(ef).duration(ti).particles(true).ambience(false).build());
                                            e.offer(data);
                                        }
                                        e.setVelocity(d2.add(d2.getX()*0.5, ((float) 4)/5f, d2.getZ()*0.5).add(e.getVelocity()));
                                    }
                                }
                            });
                        }
                    }

                    // ------------------

                    Optional<String> leapcheck = Utils.tagStartsWith(tags, "leaping");
                    if (leapcheck.isPresent()) {
                        String le = leapcheck.get();
                        int height = 5;
                        String[] sp = le.split(":");
                        if(sp.length > 1) try { height = Integer.parseInt(sp[1]); } catch (NumberFormatException ignored){ }
                        Optional<Player> p = event.getCause().first(Player.class);
                        if (p.isPresent()) {
                            Player player = p.get();
                            if (player.getVelocity().getY() < 0.3) {
                                player.setVelocity(new Vector3d(0, ((float) height) / 5f, 0).add(player.getVelocity()));
                                player.getWorld().playSound(SoundTypes.ENTITY_SLIME_SQUISH, player.getPosition(), 1f);
                                player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.HAPPY_VILLAGER).quantity(30).velocity(player.getVelocity().add(0, 0.1, 0)).build(), player.getPosition().add(0, 2, 0));
                            }
                        }
                    }

                    // ------------------

                    Optional<String> damagecheck = Utils.tagStartsWith(tags, "damaging");
                    if (damagecheck.isPresent()) {
                        String le = damagecheck.get();
                        int rad = 5;
                        int time = 4;
                        String[] sp = le.split(":");
                        if(sp.length > 1) try { rad = Integer.parseInt(sp[1]); } catch (NumberFormatException ignored){ }
                        if(sp.length > 2) try { time = Integer.parseInt(sp[2]); } catch (NumberFormatException ignored){ }

                        final int ti = time;

                        Optional<Player> p = event.getCause().first(Player.class);
                        if (p.isPresent()) {
                            Player player = p.get();
                            player.getNearbyEntities(rad).forEach(e -> {
                                if(e.getUniqueId() != player.getUniqueId()) {
                                    if(!e.getType().getId().startsWith("fossils")) {
                                        e.damage(7, DamageSources.WITHER);
                                        Optional<PotionEffectData> d = e.getOrCreate(PotionEffectData.class);
                                        if(d.isPresent()){
                                            PotionEffectData data = d.get();
                                            data.addElement(PotionEffect.builder().potionType(PotionEffectTypes.POISON).amplifier(2).duration(ti).particles(true).ambience(false).build());
                                            e.offer(data);
                                            Utils.spawnFang(e.getLocation());
                                        }
                                        player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.DAMAGE_INDICATOR).quantity(15).build(), e.getLocation().getPosition().add(0, 1.5, 0));
                                    }
                                }
                            });
                            player.getWorld().playSound(SoundTypes.ENTITY_ELDER_GUARDIAN_CURSE, player.getPosition(), 1f);
                            player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.HAPPY_VILLAGER).quantity(30).velocity(player.getVelocity().add(0, 0.1, 0)).build(), player.getPosition().add(0, 2, 0));

                        }
                    }

                    // ------------------

                    Optional<String> slowcheck = Utils.tagStartsWith(tags, "slowing");
                    if (slowcheck.isPresent()) {
                        String le = slowcheck.get();
                        int rad = 5;
                        int slow = 3;
                        int dur = 10;
                        String[] sp = le.split(":");
                        if(sp.length > 1) try { slow = Integer.parseInt(sp[1]); } catch (NumberFormatException ignored){ }
                        if(sp.length > 2) try { rad = Integer.parseInt(sp[2]); } catch (NumberFormatException ignored){ }
                        if(sp.length > 3) try { dur = Integer.parseInt(sp[3]); } catch (NumberFormatException ignored){ }
                        final int ti = dur * 20;
                        final int ef = slow;
                        Optional<Player> p = event.getCause().first(Player.class);
                        if (p.isPresent()) {
                            Player player = p.get();
                            if (player.getVelocity().getY() < 0.3) {
                                player.getNearbyEntities(rad).forEach(e -> {
                                    if(e.getUniqueId() != player.getUniqueId()) {
                                        Optional<PotionEffectData> d = e.getOrCreate(PotionEffectData.class);
                                        if(d.isPresent()){
                                            PotionEffectData data = d.get();
                                            data.addElement(PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).amplifier(ef).duration(ti).particles(true).ambience(false).build());
                                            e.offer(data);
                                            Utils.spawnFang(e.getLocation());
                                        }
                                    }
                                });
                                player.getWorld().playSound(SoundTypes.ENTITY_WITHER_DEATH, player.getPosition(), 1f, 0.5d);
                                player.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.HAPPY_VILLAGER).quantity(30).velocity(player.getVelocity().add(0, 0.1, 0)).build(), player.getPosition().add(0, 2, 0));
                            }
                        }
                    }
                }
            }
        }
        // -----------------------------
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event){
        if(event.getTargetEntity() instanceof Player){
            Optional<DamageSource> source = event.getCause().first(DamageSource.class);
            if(source.isPresent()){
                if(source.get().getType() == DamageTypes.MAGMA || source.get().getType() == DamageTypes.FIRE){
                    Player p = (Player) event.getTargetEntity();
                    Optional<ItemStack> i = p.getHelmet();
                    if(i.isPresent()){
                        ItemStack helm = i.get();
                        Optional<EnchantmentData> e = helm.get(EnchantmentData.class);
                        if(e.isPresent()){
                            if(e.get().enchantments().contains(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1))){
                                p.setHelmet(ItemStack.builder().itemType(ItemTypes.AIR).build());
                            }
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event){
        for(Entity e:event.getEntities()){
            if(e.getType() == EntityTypes.ZOMBIE){
                int chance = new Random().nextInt(10);
                if(chance == 0){
                    Utils.spawnPumplinZombie(e.getLocation());
                    e.remove();
                }
            }
        }
    }

    @Listener
    public void onExplosion(ChangeBlockEvent event){
        if(event instanceof ExplosionEvent){
            event.getTransactions().forEach(blockSnapshotTransaction -> {
                blockSnapshotTransaction.getOriginal().restore(true, BlockChangeFlags.NONE);
            });
        }
    }

    @Listener
    public void onDropItem(DropItemEvent.Destruct event){
        Optional<Zombie> e = event.getCause().first(Zombie.class);
        if(e.isPresent()){
            Zombie z = e.get();
            Optional<ItemStack> is = z.getHelmet();
            if(is.isPresent()){
                ItemStack stack = is.get();
                if(stack.getType() == ItemTypes.PUMPKIN) { // Likely a pumplin
                    if (!z.adult().get()) {
                        Optional<List<Text>> t = stack.get(Keys.ITEM_LORE);
                        if (t.isPresent()) {
                            List<Text> text = t.get();
                            if (text.size() >= 2) {
                                switch (text.get(0).toPlain().toLowerCase()) {
                                    case "lucky_pumplin":
                                        int chance = new Random().nextInt(2); // The chance is 1 / (Value)
                                        if (chance == 0) {
                                            int charmLevel = new Random().nextInt(5) + 1;
                                            ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.GOLD_NUGGET).quantity(1).build();
                                            itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Lucky Charm?"));
                                            itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                                                    Text.of(TextColors.BLACK, "fake_charm"),
                                                    Text.of(TextColors.DARK_GRAY, "Lucky Charm: 0"),
                                                    Text.of(""),
                                                    Text.of(TextColors.GOLD, TextStyles.BOLD, "TRICK!"),
                                                    Text.of(TextColors.DARK_GRAY, "It's a fake! Ha!")
                                            ));
                                            ItemStackSnapshot s = itemStack.createSnapshot();
                                            Entity f = z.getWorld().createEntity(EntityTypes.ITEM, z.getLocation().getPosition().add(0, 0.25, 0));
                                            f.offer(Keys.REPRESENTED_ITEM, s);
                                            z.getWorld().spawnEntity(f);
                                            break;
                                        }
                                        event.setCancelled(true);
                                        break;
                                    case "natural_pumplin":
                                        event.setCancelled(true);
                                        chance = new Random().nextInt(4); // The chance is 1 / (Value)
                                        if (chance == 0) {
                                            int charmLevel = new Random().nextInt(5) + 1;
                                            ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.GOLD_NUGGET).quantity(1).build();
                                            itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Lucky Charm!"));
                                            itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                                                    Text.of(TextColors.BLACK, String.format("charm-%s", charmLevel)),
                                                    Text.of(TextColors.RED, String.format("Lucky Charm: +%s", charmLevel)),
                                                    Text.of(""),
                                                    Text.of(TextColors.DARK_GRAY, "Hold this in your ", TextColors.GOLD, TextStyles.BOLD, "Off-Hand"),
                                                    Text.of(TextColors.DARK_GRAY, "to increase your luck when"),
                                                    Text.of(TextColors.DARK_GRAY, "opening blocks!")
                                            ));
                                            ItemStackSnapshot s = itemStack.createSnapshot();
                                            Entity f = z.getWorld().createEntity(EntityTypes.ITEM, z.getLocation().getPosition().add(0, 0.25, 0));
                                            f.offer(Keys.REPRESENTED_ITEM, s);
                                            z.getWorld().spawnEntity(f);
                                            break;
                                        }
                                        if (chance == 1) {
                                            int charmLevel = new Random().nextInt(5) + 1;
                                            ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.GOLD_NUGGET).quantity(1).build();
                                            itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Lucky Charm?"));
                                            itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                                                    Text.of(TextColors.BLACK, "fake_charm"),
                                                    Text.of(TextColors.DARK_GRAY, "Lucky Charm: 0"),
                                                    Text.of(""),
                                                    Text.of(TextColors.GOLD, TextStyles.BOLD, "TRICK!"),
                                                    Text.of(TextColors.DARK_GRAY, "It's a fake! Ha!")
                                            ));
                                            ItemStackSnapshot s = itemStack.createSnapshot();
                                            Entity f = z.getWorld().createEntity(EntityTypes.ITEM, z.getLocation().getPosition().add(0, 0.25, 0));
                                            f.offer(Keys.REPRESENTED_ITEM, s);
                                            z.getWorld().spawnEntity(f);
                                            break;
                                        }
                                    default:
                                        logger.info("Pumpkin Zombie death hm?");
                                }
                            }
                        }
                    }
                }
            }
        }
        Optional<WitherSkeleton> we = event.getCause().first(WitherSkeleton.class);
        if(we.isPresent()){
            WitherSkeleton w = we.get();
            Optional<ItemStack> is = w.getHelmet();
            if(is.isPresent()){
                ItemStack stack = is.get();
                if(stack.getType() == ItemTypes.LIT_PUMPKIN) { // Likely a pumplin
                    Optional<List<Text>> t = stack.get(Keys.ITEM_LORE);
                    if (t.isPresent()) {
                        List<Text> text = t.get();
                        if (text.size() >= 2) {
                            switch (text.get(0).toPlain().toLowerCase()) {
                                case "boss_pumpking":
                                    event.setCancelled(true);
                                    ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.LIT_PUMPKIN).quantity(1).build();
                                    itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Pumpkin King Trophy"));
                                    itemStack.offer(Keys.ITEM_LORE, Arrays.asList(
                                            Text.of(TextColors.BLACK, "trophy"),
                                            Text.of(TextColors.DARK_GRAY, "Earned by defeating a"),
                                            Text.of(TextColors.DARK_GRAY, "Pumpkin King during"),
                                            Text.of(TextColors.GOLD, "Halloween 2019")
                                    ));
                                    ItemStackSnapshot s = itemStack.createSnapshot();
                                    Entity f = w.getWorld().createEntity(EntityTypes.ITEM, w.getLocation().getPosition().add(0, 0.25, 0));
                                    f.offer(Keys.REPRESENTED_ITEM, s);
                                    w.getWorld().spawnEntity(f);
                                    break;
                                default:
                                    logger.info("When do wither skeletons have jack o lanterns for helmets?");
                            }
                        }
                    }

                }
            }
        }
    }

    public void loadConfig(){
        File dir = new File("./config");
        if (!dir.isDirectory()) dir.mkdirs();

        File f = new File("./config/pumpkins.json");
        if(!f.exists()){
            configuration = new PumpkinConfiguration().setDefaults();
            try {
                f.createNewFile();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(new PumpkinConfiguration().setDefaults());
                FileWriter w = new FileWriter(f);
                BufferedWriter write = new BufferedWriter(w);
                write.write(json);
                write.close();
            } catch (Exception err){
                getLogger().info("Unable to load config. Setting to defaults...");
            }
        }

        String json = "";
        // Stage: Read
        try {
            FileReader reader = new FileReader(f);
            BufferedReader r = new BufferedReader(reader);
            Iterator<String> i = r.lines().iterator();

            while(i.hasNext()){
                String next = i.next();
                json = json.concat(next);
            }
            r.close();
        } catch (Exception err){
            getLogger().info("An error occured while opening the config at "+f.getAbsolutePath());
            configuration = new PumpkinConfiguration().setDefaults();
            err.printStackTrace();
            return;
        }
        // Stage: Parse

        Gson gson = new Gson();
        try {
            configuration = gson.fromJson(json, PumpkinConfiguration.class);
            configuration.validate();
            if(configuration == null) throw new MalformedLootPoolException("The config was empty? Actually write something in the json file.");
        } catch(JsonSyntaxException err){
            getLogger().info("Malformed json in config at "+f.getAbsolutePath());
            configuration = new PumpkinConfiguration().setDefaults();
        } catch (Exception err){
            getLogger().info("An error occured while processing the config at "+f.getAbsolutePath());
            configuration = new PumpkinConfiguration().setDefaults();
        }
    }

    public void addCooldown(String cooldown_string, ItemStackSnapshot s){
        String[] split = cooldown_string.split(Pattern.quote(":"));
        if(split.length < 2) {
            logger.warn("An item has a broken cooldown tag. Fix it. I don't know what it is.");
        } else {
            try{
                int i = Integer.parseInt(split[1]);
                cooldowns.put(s, LocalDateTime.now().plusSeconds(i));
            } catch (NumberFormatException err){
                logger.warn("An item has a broken cooldown tag (time is not a number). Fix it. I don't know what it is.");
            }
        }
    }

    public void registerIgnoreUUID(UUID uuid){ this.ignoreIDs.add(uuid); }

    public static PumpkinsPlugin getPumpkinsPlugin() { return pumpkinsPlugin; }
    public static Supplykeys getSKPlugin(){ return supplykeys; }

    public Logger getLogger() { return logger; }

    public PumpkinResultManager getPumpkinResultManager() { return pumpkinResultManager; }

    public PumpkinConfiguration getConfiguration() { return configuration; }
}
