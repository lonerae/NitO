package com.lonerae.nightsintheoutskirts.game.roles;

import com.lonerae.nightsintheoutskirts.game.GameData;

public class Necromancer extends Role {

    public Necromancer() {
        setDescription(GameData.getRoleStrings().format("necromancerDescription"));
        setIconPath("data/roles/necromancer.png");
        setName(RoleName.NECROMANCER);
        setAlliance(AllianceName.ORDER);
    }
}
