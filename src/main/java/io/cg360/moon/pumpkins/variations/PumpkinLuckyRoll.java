package io.cg360.moon.pumpkins.variations;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class PumpkinLuckyRoll {

    private String id;
    private Integer weight;

    private PumpkinType type;

    public void validate(){
        if(id == null) id = "random_id";
        if(weight == null) weight = 5;

        if(type == null) type = PumpkinType.MIXED;
    }

    public final PumpkinLuckyRoll setConfig(String id, Integer weight, PumpkinType type){
        this.id = id;
        this.weight = weight;
        this.type = type;
        this.validate();
        return this;
    }

    public final void execute(Player player, Location<World> loc, int luckmult){
        validate();
        run(player, loc, luckmult);
    }

    protected abstract void run(Player player, Location<World> loc, int luckmult);

    public final String getId() { return id; }
    public final Integer getWeight(int luckmult) {
        if(type == PumpkinType.LUCKY) {
            return weight * luckmult;
        } else {
            return weight;
        }
    }
}
