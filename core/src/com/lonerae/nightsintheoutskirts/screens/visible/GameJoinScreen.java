package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class GameJoinScreen extends BaseScreen {

    private Client client;

    public GameJoinScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().format("join"), getSkin());
        UIUtil.title(title);
        TextButton searchGamesButton = new TextButton(getStrings().format("searchGames"), getSkin());
        searchGamesButton.getLabel().setWrap(true);

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(searchGamesButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

        searchGamesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MatchClient.createClient();
                client = MatchClient.getClient();
                List<InetAddress> servers = client.discoverHosts(54777, 5000);
                for (InetAddress address : servers) {
                    try {
                        client.connect(5000, address, 54555, 54777);
                        GreetingRequest request = new GreetingRequest();
                        client.sendTCP(request);
                    } catch (IOException e) {
                        Gdx.app.log("CONNECTION ERROR: ", e.getMessage());
                    }
                }
            }
        });
    }
}
