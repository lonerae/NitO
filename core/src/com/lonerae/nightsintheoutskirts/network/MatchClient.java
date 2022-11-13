package com.lonerae.nightsintheoutskirts.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;

public class MatchClient {

    private static Client client;

    public static void createClient() {
        if (client == null) {
            Log.set(Log.LEVEL_TRACE);

            client = new Client();
            client.start();
            NetworkUtil.register(client);
            createListener();
        }
    }

    public static Client getClient() {
        return client;
    }

    private static void createListener() {
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof GreetingResponse) {
                    GreetingResponse response = (GreetingResponse)object;
                    Gdx.app.log("SERVER: ", response.townName + " " + response.numberOfPlayers);
                }
            }
        });
    }
}
