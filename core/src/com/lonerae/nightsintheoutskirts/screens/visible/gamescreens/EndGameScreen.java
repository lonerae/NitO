package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.MatchServer;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.MenuScreen;

public class EndGameScreen extends BaseScreen {
    public EndGameScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().get("endGameTitle"), getBlackStyle());
        UIUtil.title(title);


        Label description = new CustomLabel(getGameStrings().get("endgame"), getBlackStyle());

        Label winner = new CustomLabel(MatchClient.getWinner().toString(), getBlackStyle());
        winner.setAlignment(Align.center);

        StringBuilder truthString = new StringBuilder();
        for (String player : MatchClient.getConnectedPlayersMap().keySet()) {
            truthString.append(player).append(" : ").append(MatchClient.getConnectedPlayersMap().get(player)).append("\n");
        }
        CustomDialog truthDialog = new CustomDialog("the Truth", truthString.toString(), getSkin(), getBlackStyle());

        TextButton revealButton = new CustomTextButton(getStrings().get("revealButtonText"), getSkin(), getBlackStyle());
        truthDialog.isHideable();
        revealButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                truthDialog.show(getStage());
            }
        });

        TextButton endMatchButton = new CustomTextButton(getStrings().get("endMatchButtonText"), getSkin(), getBlackStyle());
        endMatchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MatchClient.close();
                if (MatchServer.getServer() != null) {
                    MatchServer.close();
                }
                getGame().setScreen(new MenuScreen(getGame()));
            }
        });

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(winner).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(revealButton).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(endMatchButton).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
