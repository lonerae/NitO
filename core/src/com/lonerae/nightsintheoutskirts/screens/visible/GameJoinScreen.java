package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ConnectionRequest;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class GameJoinScreen extends BaseScreen {

    private static Map<String, Integer> availableMatches = new HashMap<>();
    private Table matchTable;

    public GameJoinScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().get("join"), getTitleStyle());
        UIUtil.title(title);
        TextButton searchGamesButton = new CustomTextButton(getStrings().get("searchGames"), getSkin(), getBlackStyle());
        searchGamesButton.getLabel().setWrap(true);

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(searchGamesButton).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();

        matchTable = new Table(getSkin());

        mainTable.add(matchTable);

        searchGamesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                searchForMatches();
            }
        });

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

    }

    private void searchForMatches() {
        InetAddress server = MatchClient.getClient().discoverHost(54777, 1000);

        try {
            MatchClient.getClient().connect(1000, server, 54555, 54777);
        } catch (IOException e) {
            Gdx.app.log("CONNECTION ERROR: ", e.getMessage() + " " + e.getCause());
        } catch (IllegalArgumentException e) {
            Gdx.app.log("CONNECTION DROPPED: ", "No matches found.");
        }

        if (server != null) {
            GreetingRequest request = new GreetingRequest();
            MatchClient.getClient().sendTCP(request);
            while (true) {
                availableMatches = MatchClient.getAvailableMatches();
                if (!availableMatches.isEmpty()) {
                    for (String matchTitle : availableMatches.keySet()) {
                        TextButton matchButton = createMatchButton(matchTitle);
                        matchTable.add(matchButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL).row();
                    }
                    break;
                }
            }
        }
    }

    private TextButton createMatchButton(String matchTitle) {
        TextButton matchButton = new CustomTextButton(matchTitle + "\n\n" +
                availableMatches.get(matchTitle), getSkin(), getBlackStyle());
        matchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                connectToMatch();
            }
        });
        return matchButton;
    }

    private void connectToMatch() {
        ConnectionRequest connectionRequest = new ConnectionRequest();
        MatchClient.getClient().sendTCP(connectionRequest);
        while (true) {
            try {
                if (MatchClient.isConnectionAccepted()) {
                    getGame().setScreen(new GameLobbyScreen(getGame()));
                } else {
                    showErrorDialog(getStrings().get("lobbyFullError"));
                }
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            MatchClient.terminate();
        } catch (NullPointerException ignored) {
        }
    }
}
