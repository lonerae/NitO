package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

public class MatchServer {

    private static Server server;
    private static GameData match;

    public static void createServer(GameData data) throws UnknownHostException {
        if (server == null) {

            Log.set(Log.LEVEL_TRACE);
            server = new Server();
            server.start();
            try {
                server.bind(54555, 54777);
            } catch (IOException e) {
                e.printStackTrace();
            }
            NetworkUtil.register(server);
            createListener();
            match = data;
        }
    }

    public static Server getServer() {
        return server;
    }

    public static Map<RoleName, Integer> getMatchRoleList() {
        return match.getMatchRoleList();
    }

    private static void createListener() {
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof GreetingRequest) {
                    GreetingResponse response = new GreetingResponse();
                    response.townName = match.getTownName();
                    response.numberOfPlayers = match.getNumberOfPlayers();
                    connection.sendTCP(response);
                }
            }
        });
    }
}
