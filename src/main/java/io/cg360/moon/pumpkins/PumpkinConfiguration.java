package io.cg360.moon.pumpkins;

import io.cg360.moon.supplykeys.entities.items.SerializableItem;

public class PumpkinConfiguration {

    private String pumpkin_crate_key_id;

    public void validate(){
        if(pumpkin_crate_key_id == null) this.pumpkin_crate_key_id = new SerializableItem().setDefault().getUniqueID();
    }

    public PumpkinConfiguration setDefaults(){
        this.pumpkin_crate_key_id = new SerializableItem().setDefault().getUniqueID();
        this.validate();
        return this;
    }

    public String getPumpkinCrateKeyID() { return pumpkin_crate_key_id; }

    public static PumpkinConfiguration get(){ return PumpkinsPlugin.getPumpkinsPlugin().getConfiguration(); }
}
