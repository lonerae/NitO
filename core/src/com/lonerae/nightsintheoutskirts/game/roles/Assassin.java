package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class Assassin extends Role {

    public Assassin() {
        setDescription(GameData.getRoleStrings().format("assassinDescription"));
        setIconPath("data/roles/assassin.png");
        setName(RoleName.ASSASSIN);
        setAlliance(AllianceName.CHAOS);
        setPriority(0);
    }
}
