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
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

public class DayResolutionScreen extends BaseScreen {
    public DayResolutionScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label description = new CustomLabel(getGameStrings().get("dayResolution"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table hangedTable = new Table(getSkin());
        while (true) {
            try {
                for (String player : MatchClient.getHangedList()) {
                    Label hangedLabel = new CustomLabel(player, getBlackStyle());
                    hangedTable.add(hangedLabel).width(WIDTH/5).row();
                    if (Player.getPlayer().getName().equals(player)) {
                        Player.getPlayer().setAlive(false);
                    }
                }
                break;
            } catch (NullPointerException ignored) {}
        }

        mainTable.add(hangedTable).padBottom(PAD_VERTICAL_BIG).row();

        if (Player.getPlayer().isAlive()) {
            TextButton continueButton = new CustomTextButton(getStrings().get("continueToNight"), getSkin(), getBlackStyle());
            continueButton.addListener(new ClickListener() {
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
                                        getGame().setScreen(new NightScreen(getGame()));
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
            mainTable.add(continueButton).width(DEFAULT_ACTOR_WIDTH).row();
        } else {
            new Thread(() -> {
                while (true) {
                    try {
                        if (MatchClient.isPermitted()) {
                            Gdx.app.postRunnable(() -> getGame().setScreen(new NightScreen(getGame())));
                            MatchClient.setPermitted(false);
                            break;
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }).start();
        }

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
