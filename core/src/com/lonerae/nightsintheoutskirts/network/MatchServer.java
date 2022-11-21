package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.requests.AssignRoleRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ConnectionRequest;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.network.requests.LobbyRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.responses.AssignRoleResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ConnectionResponse;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;
import com.lonerae.nightsintheoutskirts.network.responses.LobbyResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ProceedResponse;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchServer {

    private static Server server;
    private static GameData match;

    private static final Map<String, RoleName> connectedPlayersMap = new HashMap<>();
    private static List<RoleName> shuffledDeck;
    private static int connectedPlayersNumber = 0;
    private static int assignedPlayerNumber = 0;
    private static int readyPlayerNumber = 0;

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
            shuffleDeck();
        }
    }

    public static void terminate() {
        try {
            server.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Server getServer() {
        return server;
    }

    private static void shuffleDeck() {
        List<RoleName> deck = new ArrayList<>();
        for (RoleName roleName : match.getMatchRoleList().keySet()) {
            for (int i = 0; i < match.getMatchRoleList().get(roleName); i ++) {
                deck.add(roleName);
            }
        }
        Collections.shuffle(deck);
        shuffledDeck = deck;
    }

    private static void createListener() {
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof GreetingRequest) {
                    GreetingResponse response = new GreetingResponse();
                    response.townName = match.getTownName();
                    response.numberOfPlayers = match.getNumberOfPlayers();
                    connection.sendTCP(response);
                } else if (object instanceof ConnectionRequest) {
                    ConnectionResponse response = new ConnectionResponse();
                    if (connectedPlayersNumber < match.getNumberOfPlayers()) {
                        connectedPlayersNumber++;
                    }
                    response.connectionAccepted = connectedPlayersNumber < match.getNumberOfPlayers();
                    connection.sendTCP(response);
                } else if (object instanceof LobbyRequest) {
                    LobbyResponse response = new LobbyResponse();
                    response.matchRoleList = new ArrayList<>(match.getMatchRoleList().keySet());
                    connection.sendTCP(response);
                } else if (object instanceof AssignRoleRequest) {
                    AssignRoleRequest request = (AssignRoleRequest) object;
                    if (!connectedPlayersMap.containsKey(request.playerName)) {
                        AssignRoleResponse response = new AssignRoleResponse();
                        response.assignedRole = shuffledDeck.get(assignedPlayerNumber);
                        connection.sendTCP(response);
                        connectedPlayersMap.put(request.playerName, response.assignedRole);
                        assignedPlayerNumber++;
                    }
                } else if (object instanceof ProceedRequest) {
                    readyPlayerNumber++;
                    if (readyPlayerNumber == match.getNumberOfPlayers()) {
                        ProceedResponse response = new ProceedResponse();
                        response.permit = true;
                        server.sendToAllTCP(response);
                        readyPlayerNumber = 0;
                    }
                }
            }
        });
    }
}
