package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class Witch extends Role {

    public Witch() {
        setDescription(GameData.getRoleStrings().format("witchDescription"));
        setIconPath("data/roles/witch.png");
        setName(RoleName.WITCH);
        setAlliance(AllianceName.ORDER);
        setPriority(1);
    }
}
