package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class Civilian extends Role {

    public Civilian() {
        setDescription(GameData.getRoleStrings().format("civilianDescription"));
        setIconPath("data/roles/civilian.png");
        setName(RoleName.CIVILIAN);
        setAlliance(AllianceName.ORDER);
        setPriority(4);
    }
}
