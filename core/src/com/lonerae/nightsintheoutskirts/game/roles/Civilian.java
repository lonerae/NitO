package com.lonerae.nightsintheoutskirts.game.roles;

import com.badlogic.gdx.graphics.Texture;
import com.lonerae.nightsintheoutskirts.game.GameData;

public class Civilian extends Role {

    public Civilian() {
        setDescription(GameData.getRoleStrings().format("civilianDescription"));
        setIconPath("data/roles/civilian.png");
        setName(RoleName.CIVILIAN);
    }
}
