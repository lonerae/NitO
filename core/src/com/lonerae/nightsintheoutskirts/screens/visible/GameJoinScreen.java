package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ConnectionRequest;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class GameJoinScreen extends BaseScreen {

    private static Map<String, Integer> availableMatches = new HashMap<>();
    private InetAddress server;
    private boolean hasSearched = false;
    private Table matchTable;
    private Client client;

    public GameJoinScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().get("join"), getSkin());
        UIUtil.title(title);
        TextButton searchGamesButton = new TextButton(getStrings().get("searchGames"), getSkin());
        searchGamesButton.getLabel().setWrap(true);

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(searchGamesButton).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();

        matchTable = new CustomTable(false);

        mainTable.add(matchTable);
        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

        MatchClient.createClient();
        client = MatchClient.getClient();

        searchGamesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                server = client.discoverHost(54777, 1000);
                try {
                    client.connect(1000, server, 54555, 54777);
                    GreetingRequest request = new GreetingRequest();
                    MatchClient.getClient().sendTCP(request);
                    boolean flag = true;
                    while (flag) {
                        if (!MatchClient.getAvailableMatches().isEmpty()) {
                            availableMatches = MatchClient.getAvailableMatches();
                            hasSearched = true;
                            flag = false;
                        }
                    }
                } catch (IOException | IllegalArgumentException e) {
                    Gdx.app.log("CONNECTION ERROR: ", e.getMessage());
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (!availableMatches.isEmpty() && hasSearched) {
            hasSearched = false;
            for (String matchTitle : availableMatches.keySet()) {
                TextButton matchButton = new TextButton(matchTitle + "\n\n" + availableMatches.get(matchTitle), getSkin());
                matchButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ConnectionRequest connectionRequest = new ConnectionRequest();
                        client.sendTCP(connectionRequest);
                        boolean flag = true;
                        while (flag) {
                            try {
                                if (MatchClient.isConnectionAccepted()) {
                                    getGame().setScreen(new GameLobbyScreen(getGame()));
                                    flag = false;
                                } else {
                                    //error dialog
                                }
                            } catch (NullPointerException ignored) {}
                        }
                    }
                });
                matchTable.add(matchButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL).row();
            }
        }
    }
}
