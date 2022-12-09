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

import java.util.List;

public class NightResolutionScreen extends BaseScreen {
    public NightResolutionScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label description = new CustomLabel(getGameStrings().get("nightResolution"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table murderedTable = new Table(getSkin());
        waitToFillMurderedTable(murderedTable);

        mainTable.add(murderedTable).padBottom(PAD_VERTICAL_BIG).row();

        if (Player.getPlayer().isAlive()) {
            addContinueButton(mainTable);
        } else {
            waitForAlivePlayers(new DayScreen(getGame()));
        }

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void addContinueButton(Table mainTable) {
        TextButton continueButton = new CustomTextButton(getStrings().get("continueToDay"), getSkin(), getBlackStyle());
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

    private void waitToFillMurderedTable(Table murderedTable) {
        while (true) {
            try {
                List<String> murderedList = MatchClient.getMurderedList();
                fillMurderedList(murderedTable, murderedList);
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    private void fillMurderedList(Table murderedTable, List<String> murderedList) {
        for (String player : murderedList) {
            Label murderedLabel = new CustomLabel(player, getBlackStyle());
            murderedTable.add(murderedLabel).width(WIDTH / 5).row();
            if (Player.getPlayer().getName().equals(player)) {
                Player.getPlayer().setAlive(false);
            }
        }
    }
}
