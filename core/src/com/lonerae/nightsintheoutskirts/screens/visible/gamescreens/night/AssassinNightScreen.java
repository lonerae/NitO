package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

    private static Dialog overviewDialog;
    private static StringBuilder overviewMessage;
    private static StringBuilder defaultOverviewMessage;
    private final ButtonGroup<CheckBox> voteCheckGroup = new ButtonGroup<>();
    private ScrollPane scroll;

    public AssassinNightScreen(Game game) {
        super(game);
        voteCheckGroup.setMinCheckCount(0);
    }

    public static void updateOverview(String killer, String target, boolean skip) {
        if (skip) {
            int index = overviewMessage.indexOf("skip: ");
            overviewMessage.insert(index + 6, killer + " ");
        } else {
            int index = overviewMessage.indexOf(target);
            overviewMessage.insert(index + target.length() + 2, killer + " ");
        }
        overviewDialog.getContentTable().clearChildren();
        overviewDialog.getContentTable().add(new CustomLabel(overviewMessage, getBlackStyle())).width(DEFAULT_POPUP_SIZE);
    }

    public ScrollPane getScroll() {
        return scroll;
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getTitleStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(GameData.getRoleStrings().get("assassinNight"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table alivePlayerTable = new Table(getSkin());
        fillAlivePlayerTable(alivePlayerTable);

        mainTable.add(alivePlayerTable).padBottom(PAD_VERTICAL_BIG).row();

        TextButton activateButton = new CustomTextButton(getStrings().get("abilityReady"), getButtonStyle());
        TextButton continueButton = new CustomTextButton(getStrings().get("skipChoice"), getButtonStyle());

        activateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                murderOrError();
            }
        });

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                skipAbility();
            }
        });

        TextButton overviewButton = addOverviewButton();

        Table buttonTable = new Table(getSkin());
        buttonTable.add(activateButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL);
        buttonTable.add(continueButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL).row();
        buttonTable.add(overviewButton).width(DEFAULT_ACTOR_WIDTH).colspan(2).pad(PAD_HORIZONTAL_SMALL);

        mainTable.add(buttonTable);

        scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private TextButton addOverviewButton() {
        TextButton overviewButton = new CustomTextButton(getStrings().get("assassinOverview"), getButtonStyle());
        createDefaultOverviewMessage();
        overviewDialog = new CustomDialog("KILL LIST", overviewMessage.toString(), getSkin(), getBlackStyle());
        overviewButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                overviewDialog.show(getStage());
                getScroll().setFlickScroll(false);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                overviewDialog.hide();
                getScroll().setFlickScroll(true);
            }
        });
        return overviewButton;
    }

    private void createDefaultOverviewMessage() {
        overviewMessage = new StringBuilder();
        for (String player : MatchClient.getAlivePlayersMap().keySet()) {
            overviewMessage.append(player).append(": \n");
        }
        overviewMessage.append("\n");
        overviewMessage.append("skip: ");
        defaultOverviewMessage = new StringBuilder(overviewMessage);
    }

    private void skipAbility() {
        Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("assassinWaitMessage"), getSkin(), getBlackStyle());
        dialog.show(getStage());

        waitForOtherAssassinsToSkip(dialog);
    }

    private void murderOrError() {
        if (voteCheckGroup.getAllChecked().size == 1) {
            Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("assassinWaitMessage"), getSkin(), getBlackStyle());
            dialog.show(getStage());
            waitForOtherAssassinsToKill(dialog);
        } else {
            showErrorDialog(getStrings().get("noChoiceError"));
        }
    }

    private void waitForOtherAssassinsToSkip(Dialog dialog) {
        MurderRequest request = new MurderRequest();
        request.willKill = false;
        request.killer = Player.getPlayer().getName();
        MatchClient.getClient().sendTCP(request);
        new Thread(() -> {
            while (true) {
                try {
                    if (MatchClient.getAssassinPermitted()) {
                        proceed(dialog);
                    } else {
                        showNoDecision(dialog);
                        resetOverview();
                    }
                    break;
                } catch (NullPointerException ignored) {
                }
            }
        }).start();
    }

    private void resetOverview() {
        overviewMessage = new StringBuilder(defaultOverviewMessage);
        overviewDialog.getContentTable().clearChildren();
        overviewDialog.getContentTable().add(new CustomLabel(overviewMessage, getBlackStyle())).width(DEFAULT_POPUP_SIZE);
    }

    private void showNoDecision(Dialog dialog) {
        Gdx.app.postRunnable(() -> {
            dialog.hide();
            MatchClient.setAssassinPermitted(null);
            showErrorDialog(getStrings().get("noDecisionError"));
        });
    }

    private void proceed(Dialog dialog) {
        Gdx.app.postRunnable(() -> {
            ProceedRequest proceedRequest = new ProceedRequest();
            dialog.hide();
            overviewDialog = null;
            overviewMessage = null;
            defaultOverviewMessage = null;
            waitForOtherPlayers(proceedRequest, ProceedType.ABILITY, new NightResolutionScreen(getGame()));
        });
        MatchClient.setAssassinPermitted(null);
    }

    private void waitForOtherAssassinsToKill(Dialog dialog) {
        MurderRequest request = new MurderRequest();
        request.willKill = true;
        request.killer = Player.getPlayer().getName();
        request.target = voteCheckGroup.getChecked().getLabel().getText().toString();
        MatchClient.getClient().sendTCP(request);
        new Thread(() -> {
            while (true) {
                try {
                    if (MatchClient.getAssassinPermitted()) {
                        proceed(dialog);
                    } else {
                        showNoDecision(dialog);
                        resetOverview();
                    }

                    break;
                } catch (NullPointerException ignored) {
                }
            }
        }).start();
    }

    private void fillAlivePlayerTable(Table alivePlayerTable) {
        int count = 0;
        for (String playerName : MatchClient.getAlivePlayersMap().keySet()) {
            CheckBox voteCheck = createCheckBox(playerName);
            alivePlayerTable.add(voteCheck).pad(PAD_HORIZONTAL_SMALL);
            count++;

            if (count % 3 == 0) {
                alivePlayerTable.row();
            }
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
