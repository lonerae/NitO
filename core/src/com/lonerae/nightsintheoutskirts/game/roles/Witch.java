package com.lonerae.nightsintheoutskirts.game.roles;

import com.badlogic.gdx.graphics.Texture;
import com.lonerae.nightsintheoutskirts.game.GameData;

public class Witch extends Role {

    public Witch() {
        setDescription(GameData.getRoleStrings().format("witchDescription"));
        setIcon(new Texture("data/roles/witch.png"));
        setName(RoleName.WITCH);
    }
}
