package com.lonerae.nightsintheoutskirts.game.roles;

import com.badlogic.gdx.graphics.Texture;
import com.lonerae.nightsintheoutskirts.game.GameData;

public class Assassin extends Role {

    public Assassin() {
        setDescription(GameData.getRoleStrings().format("assassinDescription"));
        setIcon(new Texture("data/roles/assassin.png"));
        setName(RoleName.ASSASSIN);
    }
}
