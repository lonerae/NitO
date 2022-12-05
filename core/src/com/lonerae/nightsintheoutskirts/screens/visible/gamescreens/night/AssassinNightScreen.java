package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.MurderRequest;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.NightResolutionScreen;

public class AssassinNightScreen extends NightScreen {

    private final ButtonGroup<CheckBox> voteCheckGroup = new ButtonGroup<>();

    public AssassinNightScreen(Game game) {
        super(game);
        voteCheckGroup.setMinCheckCount(0);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getBlackStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(GameData.getRoleStrings().get("assassinNight"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table alivePlayerTable = new Table(getSkin());
        fillAlivePlayerTable(alivePlayerTable);

        mainTable.add(alivePlayerTable).row();

        TextButton activateButton = new CustomTextButton(getStrings().get("abilityReady"), getSkin(), getBlackStyle());
        TextButton continueButton = new CustomTextButton(getStrings().get("skipChoice"), getSkin(), getBlackStyle());

        activateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (voteCheckGroup.getAllChecked().size == 1) {
                    Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("assassinWaitMessage"), getSkin(), getBlackStyle());
                    dialog.show(getStage());

                    MurderRequest request = new MurderRequest();
                    request.willKill = true;
                    request.target = voteCheckGroup.getChecked().getLabel().getText().toString();
                    MatchClient.getClient().sendTCP(request);
                    new Thread(() -> {
                        while (true) {
                            try {
                                if (MatchClient.getAssassinPermitted()) {
                                    Gdx.app.postRunnable(() -> {
                                        ProceedRequest proceedRequest = new ProceedRequest();
                                        dialog.hide();
                                        waitForOtherPlayers(proceedRequest, ProceedType.ABILITY, new NightResolutionScreen(getGame()));
                                    });
                                    MatchClient.setAssassinPermitted(false);
                                    break;
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }).start();
                } else {
                    showErrorDialog(getStrings().get("noChoiceError"));
                }

            }
        });

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("assassinWaitMessage"), getSkin(), getBlackStyle());
                dialog.show(getStage());

                MurderRequest request = new MurderRequest();
                request.willKill = false;
                MatchClient.getClient().sendTCP(request);
                new Thread(() -> {
                    while (true) {
                        try {
                            if (MatchClient.getAssassinPermitted()) {
                                Gdx.app.postRunnable(() -> {
                                    ProceedRequest proceedRequest = new ProceedRequest();
                                    dialog.hide();
                                    waitForOtherPlayers(proceedRequest, ProceedType.ABILITY, new NightResolutionScreen(getGame()));
                                });
                                MatchClient.setAssassinPermitted(false);
                                break;
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }).start();
            }
        });

        Table buttonTable = new Table(getSkin());
        buttonTable.add(activateButton).width(WIDTH / 3).pad(PAD_HORIZONTAL_SMALL).row();
        buttonTable.add(continueButton).width(WIDTH / 3).colspan(2).pad(PAD_HORIZONTAL_SMALL);

        mainTable.add(buttonTable);

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void fillAlivePlayerTable(Table alivePlayerTable) {
        for (String playerName : MatchClient.getAlivePlayersMap().keySet()) {
            CheckBox voteCheck = createCheckBox(playerName);
            alivePlayerTable.add(voteCheck).pad(PAD_HORIZONTAL_SMALL);
        }
    }

    private CheckBox createCheckBox(String playerName) {
        CheckBox voteCheck = new CheckBox(playerName, getSkin());
        voteCheck.getLabel().setStyle(getBlackStyle());
        voteCheck.getImage().setScaling(Scaling.fill);
        voteCheck.getImageCell().size(WIDTH / 15);
        voteCheck.getLabelCell().padLeft(PAD_HORIZONTAL_SMALL);
        voteCheckGroup.add(voteCheck);
        return voteCheck;
    }
}
