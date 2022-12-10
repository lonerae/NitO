package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.DeadNightScreen;

import java.util.List;

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
        waitToFillHangedTable(hangedTable);

        mainTable.add(hangedTable).padBottom(PAD_VERTICAL_BIG).row();

        if (Player.getPlayer().isAlive()) {
            addContinueButton(mainTable);
        } else {
            waitForAlivePlayers(new DeadNightScreen(getGame()));
        }

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void addContinueButton(Table mainTable) {
        TextButton continueButton = new CustomTextButton(getStrings().get("continueToNight"), getSkin(), getBlackStyle());
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                continueToNight();
            }
        });
        mainTable.add(continueButton).width(DEFAULT_ACTOR_WIDTH).row();
    }

    private void continueToNight() {
        ProceedRequest request = new ProceedRequest();
        waitForOtherPlayers(request, ProceedType.END, Player.getPlayer().getNight(getGame()));
    }

    private void waitToFillHangedTable(Table hangedTable) {
        while (true) {
            try {
                List<String> hangedList = MatchClient.getHangedList();
                fillHangedTable(hangedTable, hangedList);
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    private void fillHangedTable(Table hangedTable, List<String> hangedList) {
        for (String player : hangedList) {
            Label hangedLabel = new CustomLabel(player, getBlackStyle());
            hangedTable.add(hangedLabel).width(WIDTH / 5).row();
            if (Player.getPlayer().getName().equals(player)) {
                Player.getPlayer().setAlive(false);
            }
        }
    }
}
