package com.lonerae.nightsintheoutskirts;

import com.badlogic.gdx.Game;
import com.lonerae.nightsintheoutskirts.screens.visible.LogoScreen;

public class NightsInTheOutskirts extends Game {

    @Override
    public void create() {
        setScreen(new LogoScreen(this));
    }
}
