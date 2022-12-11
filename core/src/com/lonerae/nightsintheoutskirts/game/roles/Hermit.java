package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class Hermit extends Role {

    public Hermit() {
        setDescription(GameData.getRoleStrings().format("hermitDescription"));
        setIconPath("data/roles/hermit.png");
        setName(RoleName.HERMIT);
        setAlliance(AllianceName.ORDER);
        setPriority(2);
    }
}
