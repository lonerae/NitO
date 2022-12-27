package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import static java.lang.Thread.sleep;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

import java.util.List;

public class NightResolutionScreen extends BaseScreen {

    private static boolean fourthCheck = true;
    private boolean alive = true;

    public NightResolutionScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night Resolution", getTitleStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(getGameStrings().get("nightResolution"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        CustomLabel resolutionLabel;
        Label countdownLabel = new CustomLabel("3", getBlackStyle());
        if (Player.getPlayer().isAlive()) {
            resolutionLabel = waitForResolutionLabel();
        } else {
            resolutionLabel = new CustomLabel(getGameStrings().get("deadResolution"), getBlackStyle());
            resolutionLabel.setAlignment(Align.center);
            alive = false;
        }

        new Thread(() -> {
            try {
                if (alive) {
                    mainTable.add(countdownLabel).row();
                    countdownLabel.setAlignment(Align.center);
                    sleep(1500);
                    countdownLabel.setText("2");
                    sleep(1500);
                    countdownLabel.setText("1");
                    sleep(1500);
                    mainTable.removeActor(countdownLabel);
                }
            } catch (InterruptedException ignored) {
            }
            Gdx.app.postRunnable(() -> {
                mainTable.add(resolutionLabel).padBottom(PAD_VERTICAL_BIG).row();
                fourthCivilianCheck(mainTable);
                if (Player.getPlayer().isAlive()) {
                    addContinueButton(mainTable);
                } else {
                    waitForAlivePlayers(new DayScreen(getGame()));
                }
            });
        }).start();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void fourthCivilianCheck(Table mainTable) {
        if (fourthCheck && Player.getPlayer().getRole().getName().equals(RoleName.FOURTH_CIVILIAN)
                && !Player.getPlayer().isAbleToUseAbility()
                && Player.getPlayer().isAlive()) {
            RoleName currentRole = MatchClient.getMatchClientInstance().getAlivePlayersMap().get(Player.getPlayer().getName());
            if (!currentRole.equals(RoleName.FOURTH_CIVILIAN)) {
                Label updatedRole = new CustomLabel(getGameStrings().get("updatedRole") + " " +
                        currentRole + ".)", getBlackStyle());
                mainTable.add(updatedRole).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
                Player.getPlayer().setRole(Role.getRole(currentRole));
            } else {
                Label updatedRole = new CustomLabel(getGameStrings().get("failedToUpdateRole"), getBlackStyle());
                mainTable.add(updatedRole).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
            }
            fourthCheck = false;
        }
    }

    private void addContinueButton(Table mainTable) {
        TextButton continueButton = new CustomTextButton(getStrings().get("continueToDay"), getButtonStyle());
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                continueToDay();
            }
        });
        mainTable.add(continueButton).width(DEFAULT_ACTOR_WIDTH).row();
    }

    private void continueToDay() {
        ProceedRequest request = new ProceedRequest();
        waitForOtherPlayers(request, ProceedType.END, new DayScreen(getGame()));
    }

    private CustomLabel waitForResolutionLabel() {
        CustomLabel resolutionLabel;
        while (true) {
            try {
                List<String> murderedList = MatchClient.getMatchClientInstance().getMurderedList();
                if (murderedList.contains(Player.getPlayer().getName())) {
                    resolutionLabel = new CustomLabel(getGameStrings().get("deadResolution"), getBlackStyle());
                    Player.getPlayer().setAlive(false);
                } else {
                    resolutionLabel = new CustomLabel(getGameStrings().get("safeResolution"), getBlackStyle());
                }
                break;
            } catch (NullPointerException ignored) {
            }
        }
        resolutionLabel.setAlignment(Align.center);
        return resolutionLabel;
    }
}
