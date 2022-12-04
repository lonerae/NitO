package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;

public class DayResolutionScreen extends BaseScreen {
    public DayResolutionScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        while (true) {
            try {
                for (String player : MatchClient.getHangedList()) {
                    System.out.println(player);
                }
                break;
            } catch (NullPointerException ignored) {}
        }
    }
}
