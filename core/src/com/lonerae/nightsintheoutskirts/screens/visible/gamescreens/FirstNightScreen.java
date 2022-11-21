package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

public class FirstNightScreen extends BaseScreen {

    public FirstNightScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getSkin());
        UIUtil.title(title);

        Label description = new CustomLabel(getGameStrings().get("firstNight"), getSkin());

        Table playerTable = new Table();
        int counter = 0;
        for (String player : MatchClient.getConnectedPlayersMap().keySet()) {
            Label playerLabel = new CustomLabel(player, getSkin());
            if (MatchClient.getConnectedPlayersMap().get(player).equals(RoleName.ASSASSIN)) {
                if (Player.getPlayer().getRole().getName().equals(RoleName.ASSASSIN)) {
                    playerLabel = new CustomLabel(player, getSkin(), "font", Color.RED);
                }
            }
            playerTable.add(playerLabel).width(WIDTH / 3);
            counter++;
            if (counter % 3 == 0) {
                playerTable.row();
            }
        }

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(description).padBottom(PAD_VERTICAL_BIG).width(DEFAULT_ACTOR_WIDTH).row();
        mainTable.add(playerTable).padBottom(PAD_VERTICAL_BIG).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

    }
}
