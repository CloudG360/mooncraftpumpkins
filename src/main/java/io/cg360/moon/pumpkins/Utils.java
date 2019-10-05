package io.cg360.moon.pumpkins;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * (C) Copyright 2019 - Will Scully (CloudGamer360), All rights reserved
 *
 * By using the following application, you are accepting all responsibility
 * for any damage or legal issues which arise over it. Additionally, you are
 * agreeing not to use this application or it's components anywhere other
 * than the Mooncraft Minecraft Server unless you have written permission from
 * the copyright holder.
 *
 *
 * Failure to follow the license will result in a termination of license.
 */
public class Utils {

    public static Optional<String> tagStartsWith(String[] tags, String beginsWith){
        for(String string:tags){
            if(string.toLowerCase().startsWith(beginsWith.toLowerCase())){
                return Optional.of(string);
            }
        }
        return Optional.empty();
    }

    public static String compareDateTimes(LocalDateTime early, LocalDateTime late){
        Duration d = Duration.between(early, late);
        String build = "";

        long sec = d.get(ChronoUnit.SECONDS)+1;
        long last = 0;

        long days = 0;
        last = sec;
        sec = sec % (60 * 60 * 24);
        if(last != sec) days = (last - sec) / (60 * 60 * 24);

        long hours = 0;
        last = sec;
        sec = sec % (60 * 60);
        if(last != sec) hours = (last - sec) / (60 * 60);

        long minutes = 0;
        last = sec;
        sec = sec % 60;
        if(last != sec) minutes = (last - sec) / 60;

        long seconds = sec;

        if(days > 0) build = build.concat(days + " day(s), ");
        if(hours > 0) build = build.concat(hours + " hour(s), ");
        if(minutes > 0) build = build.concat(minutes + " minute(s), ");
        if(seconds > 0) build = build.concat(seconds + " second(s), ");

        try {
            return build.substring(0, build.length() - 2);
        } catch (Exception err){
            return "<1 second(s) ";
        }
    }

    public static Entity spawnFang(Location<World> loc){
        Entity e = loc.getExtent().createEntity(EntityTypes.EVOCATION_FANGS, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(Utils.parseToSpongeString("&6&lPumplin &8&lMinion")));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        e.offer(Keys.ATTACK_DAMAGE, 0d);
        e.offer(Keys.DAMAGE_ENTITY_MAP, new HashMap<>());
        PumpkinsPlugin.getPumpkinsPlugin().registerIgnoreUUID(e.getUniqueId());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(e);
        }
        return e;
    }

    public static Entity spawnPumplinZombie(Location<World> loc){
        Entity e = loc.getExtent().createEntity(EntityTypes.ZOMBIE, loc.getPosition());
        e.offer(Keys.DISPLAY_NAME, Text.of(Utils.parseToSpongeString("&6&lPumplin &8&lMinion")));
        e.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        Zombie z = (Zombie) e;

        ItemStack pk = ItemStack.builder().itemType(ItemTypes.PUMPKIN).quantity(1).build();
        Optional<EnchantmentData> dat = pk.getOrCreate(EnchantmentData.class);
        if(dat.isPresent()){
            EnchantmentData data = dat.get();
            data.set(dat.get().enchantments().add(Enchantment.of(EnchantmentTypes.BINDING_CURSE, 1)));
            pk.offer(data);
        }
        pk.offer(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.DARK_GRAY, "Cursed ", TextColors.GOLD, "Pumpkin"));
        pk.offer(Keys.ITEM_LORE, Arrays.asList(
                Text.of(TextColors.BLACK, "natural_pumplin"),
                Text.of(TextColors.RED, "You should not have this. It's a bug.")
        ));

        ItemStackSnapshot helm = pk.createSnapshot();

        z.setHelmet(pk);
        z.offer(Keys.IS_ADULT, false);

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            loc.getExtent().spawnEntity(z);
        }
        return z;
    }

    public static String pickRandomFromList(String[] list){
         Random random = new Random();
         return list[random.nextInt(list.length)];
    }

    public static void givePlayerItem(UUID uuid, ItemStackSnapshot snapshot){
        if(uuid == null) return;
        if(snapshot == null) return;
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        Optional<User> u =  userStorage.get().get(uuid);
        if(!u.isPresent()){
            PumpkinsPlugin.getPumpkinsPlugin().getLogger().error("Player @ "+uuid.toString()+" doesn't exist");
            return;
        }

        User user = u.get();

        if(user.isOnline()){
            Optional<Player> p = user.getPlayer();
            if(!p.isPresent()){
                PumpkinsPlugin.getPumpkinsPlugin().getLogger().error("Player @ "+uuid.toString()+" is online but doesn't exist");
                return;
            }
            Player player = p.get();
            InventoryTransactionResult r = player.getInventory().offer(snapshot.createStack());
            if(r.getType() != InventoryTransactionResult.Type.SUCCESS){
                PumpkinsPlugin.getPumpkinsPlugin().getLogger().error("Player @ "+player.getName()+" had a full inventory. Replacing offhand");
                player.setItemInHand(HandTypes.OFF_HAND, snapshot.createStack());
            }
        } else {
            InventoryTransactionResult r = user.getInventory().offer(snapshot.createStack());
            if(r.getType() != InventoryTransactionResult.Type.SUCCESS){
                PumpkinsPlugin.getPumpkinsPlugin().getLogger().error("Player @ "+uuid.toString()+" had a full inventory. Replacing offhand");
                user.setItemInHand(HandTypes.OFF_HAND, snapshot.createStack());
            }
        }

    }

    public static void messageToWorld(World world, Text text){
        for(Player player:world.getPlayers()){
            player.sendMessage(text);
        }
    }

    public static void messageToServer(Text text){
        for(Player player: Sponge.getServer().getOnlinePlayers()){
            player.sendMessage(text);
        }
    }

    public static void soundToWorld(World world, SoundType type, double volume){
        for(Player player:world.getPlayers()){
            player.playSound(type, player.getPosition(), volume);
        }
    }

    public static String generateLocationID(Location<World> loc){
        return String.format("%s:%f,%f,%f", loc.getExtent(), Math.floor(loc.getX()), Math.floor(loc.getY()), Math.floor(loc.getZ()));
    }

    public static void fillInventory(Inventory inventory, ItemStack[] items){
        Iterator<Inventory> in = inventory.slots().iterator();
        int iterate = 0;

        for(ItemStack is:items) {
            if(in.hasNext()){
                InventoryTransactionResult t = in.next().set(is);
            }
        }
    }

    public static Object[] parseToSpongeString(String string){
        ArrayList<Object> objs = new ArrayList<Object>();
        String[] codes = string.split("&");
        objs.add(codes[0]);
        for(int i = 1; i < codes.length; i++){
            if(codes[i] == null) continue;
            if(codes[i].equals("")) continue;
            switch (codes[i].toLowerCase().charAt(0)){
                case '0':
                    objs.add(TextColors.BLACK);
                    objs.add(codes[i].substring(1));
                    break;
                case '1':
                    objs.add(TextColors.DARK_BLUE);
                    objs.add(codes[i].substring(1));
                    break;
                case '2':
                    objs.add(TextColors.DARK_GREEN);
                    objs.add(codes[i].substring(1));
                    break;
                case '3':
                    objs.add(TextColors.DARK_AQUA);
                    objs.add(codes[i].substring(1));
                    break;
                case '4':
                    objs.add(TextColors.DARK_RED);
                    objs.add(codes[i].substring(1));
                    break;
                case '5':
                    objs.add(TextColors.DARK_PURPLE);
                    objs.add(codes[i].substring(1));
                    break;
                case '6':
                    objs.add(TextColors.GOLD);
                    objs.add(codes[i].substring(1));
                    break;
                case '7':
                    objs.add(TextColors.GRAY);
                    objs.add(codes[i].substring(1));
                    break;
                case '8':
                    objs.add(TextColors.DARK_GRAY);
                    objs.add(codes[i].substring(1));
                    break;
                case '9':
                    objs.add(TextColors.BLUE);
                    objs.add(codes[i].substring(1));
                    break;
                case 'a':
                    objs.add(TextColors.GREEN);
                    objs.add(codes[i].substring(1));
                    break;
                case 'b':
                    objs.add(TextColors.AQUA);
                    objs.add(codes[i].substring(1));
                    break;
                case 'c':
                    objs.add(TextColors.RED);
                    objs.add(codes[i].substring(1));
                    break;
                case 'd':
                    objs.add(TextColors.LIGHT_PURPLE);
                    objs.add(codes[i].substring(1));
                    break;
                case 'e':
                    objs.add(TextColors.YELLOW);
                    objs.add(codes[i].substring(1));
                    break;
                case 'f':
                    objs.add(TextColors.WHITE);
                    objs.add(codes[i].substring(1));
                    break;
                case 'k':
                    objs.add(TextStyles.OBFUSCATED);
                    objs.add(codes[i].substring(1));
                    break;
                case 'l':
                    objs.add(TextStyles.BOLD);
                    objs.add(codes[i].substring(1));
                    break;
                case 'm':
                    objs.add(TextStyles.STRIKETHROUGH);
                    objs.add(codes[i].substring(1));
                    break;
                case 'n':
                    objs.add(TextStyles.UNDERLINE);
                    objs.add(codes[i].substring(1));
                    break;
                case 'o':
                    objs.add(TextStyles.ITALIC);
                    objs.add(codes[i].substring(1));
                    break;
                case 'r':
                    objs.add(TextStyles.RESET);
                    objs.add(TextColors.RESET);
                    objs.add(codes[i].substring(1));
                    break;
                case 'x':
                    objs.add(String.valueOf(new Random().nextInt(1000000000)));
                    objs.add(codes[i].substring(1));
                    break;
                default:
                    objs.add(codes[i]);
                    break;
            }
        }
        return objs.toArray();
    }


}
