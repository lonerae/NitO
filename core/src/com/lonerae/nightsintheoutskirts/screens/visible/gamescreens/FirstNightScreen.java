package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

public class FirstNightScreen extends BaseScreen {

    public FirstNightScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getTitleStyle());
        UIUtil.title(title);

        Label description = new CustomLabel(getGameStrings().get("firstNight"), getBlackStyle());

        Table playerTable = new Table();
        int counter = 0;
        for (String player : MatchClient.getAlivePlayersMap().keySet()) {
            Label playerLabel = new CustomLabel(player, getBlackStyle());
            if (MatchClient.getAlivePlayersMap().get(player).equals(RoleName.ASSASSIN)) {
                if (Player.getPlayer().getRole().getName().equals(RoleName.ASSASSIN)) {
                    playerLabel = new CustomLabel(player, getRedStyle());
                }
            }
            playerLabel.setAlignment(Align.center);
            playerTable.add(playerLabel).width(WIDTH / 3);
            counter++;
            if (counter % 3 == 0) {
                playerTable.row();
            }
        }

        TextButton startButton = new CustomTextButton(getStrings().get("startFirstDay"), getSkin(), getBlackStyle());
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("waitMessage"), getSkin(), getBlackStyle());
                dialog.show(getStage());
                ProceedRequest request = new ProceedRequest();
                MatchClient.getClient().sendTCP(request);
                new Thread(() -> {
                    while (true) {
                        try {
                            if (MatchClient.isPermitted()) {
                                Gdx.app.postRunnable(() -> {
                                    dialog.hide();
                                    getGame().setScreen(new DayScreen(getGame()));
                                });
                                MatchClient.setPermitted(false);
                                break;
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }).start();
            }
        });

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(description).padBottom(PAD_VERTICAL_BIG).width(DEFAULT_ACTOR_WIDTH).row();
        mainTable.add(playerTable).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(startButton).padBottom(PAD_VERTICAL_SMALL).width(DEFAULT_ACTOR_WIDTH).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

    }
}
