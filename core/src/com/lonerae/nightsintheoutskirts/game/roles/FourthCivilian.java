package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class FourthCivilian extends Role {
    public FourthCivilian() {
        setDescription(GameData.getRoleStrings().format("fourthCivilianDescription"));
        setIconPath("data/roles/fourth_civilian.png");
        setName(RoleName.FOURTH_CIVILIAN);
        setAlliance(AllianceName.ORDER);
    }
}
