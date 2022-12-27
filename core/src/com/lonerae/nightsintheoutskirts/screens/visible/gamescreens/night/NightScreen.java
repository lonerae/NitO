package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night;

import com.badlogic.gdx.Game;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;

public class NightScreen extends BaseScreen {
    public NightScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        (new Thread(this::checkEndGame)).start();
    }
}
