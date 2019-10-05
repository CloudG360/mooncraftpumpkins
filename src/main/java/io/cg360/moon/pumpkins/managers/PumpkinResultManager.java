package io.cg360.moon.pumpkins.managers;

import io.cg360.moon.pumpkins.PumpkinsPlugin;
import io.cg360.moon.pumpkins.variations.PumpkinLuckyRoll;

import java.util.HashMap;
import java.util.Optional;

public class PumpkinResultManager {

    private HashMap<String, PumpkinLuckyRoll> pumpkinRollMap;

    public PumpkinResultManager() {
        this.pumpkinRollMap = new HashMap<>();
    }

    public void registerRoll(PumpkinLuckyRoll roll){
        if(roll == null) {
            PumpkinsPlugin.getPumpkinsPlugin().getLogger().warn("Roll is null!");
            return;
        }
        roll.validate();
        pumpkinRollMap.put(roll.getId().toLowerCase(), roll);
    }

    public Optional<PumpkinLuckyRoll> getRoll(String id){ return pumpkinRollMap.containsKey(id.toLowerCase()) ? Optional.of(pumpkinRollMap.get(id.toLowerCase())) : Optional.empty(); }
    public HashMap<String, PumpkinLuckyRoll> getPumpkinRollMap() { return pumpkinRollMap; }
    public HashMap<String, String> getAutoCompleteMap() {
        HashMap<String, String> newMap = new HashMap<>();
        for(String key: pumpkinRollMap.keySet()){
            newMap.put(key, key);
        }
        return newMap;
    }
}
