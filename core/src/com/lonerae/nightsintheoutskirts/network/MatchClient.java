package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.responses.AssignRoleResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ConnectionResponse;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;
import com.lonerae.nightsintheoutskirts.network.responses.LobbyResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ProceedResponse;
import com.lonerae.nightsintheoutskirts.network.responses.VoteResponse;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.DayScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchClient {

    private static Client client;
    private static final Map<String, Integer> availableMatches = new HashMap<>();

    private static List<RoleName> matchRoleList;
    private static Boolean connectionAccepted = null;
    private static Role assignedRole;
    private static Boolean permitted = null;

    private static HashMap<String, RoleName> alivePlayersMap;
    private static HashMap<String, RoleName> deadPlayersMap;
    private static List<String> hangedList;

    public static void terminate() {
        try {
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client();
            client.start();
            NetworkUtil.register(client);
            createListener();
        }
        return client;
    }

    public static Map<String, Integer> getAvailableMatches() {
        return availableMatches;
    }

    public static List<RoleName> getMatchRoleList() {
        return matchRoleList;
    }

    public static Boolean isConnectionAccepted() {
        return connectionAccepted;
    }

    public static Role getAssignedRole() {
        return assignedRole;
    }

    public static Boolean isPermitted() {
        return permitted;
    }

    public static void setPermitted(Boolean permitted) {
        MatchClient.permitted = permitted;
    }

    public static HashMap<String, RoleName> getAlivePlayersMap() {
        return alivePlayersMap;
    }

    public static HashMap<String, RoleName> getDeadPlayersMap() {
        return deadPlayersMap;
    }

    public static List<String> getHangedList() {
        return hangedList;
    }

    private static void createListener() {
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GreetingResponse) {
                    GreetingResponse response = (GreetingResponse) object;
                    availableMatches.put(response.townName, response.numberOfPlayers);
                } else if (object instanceof ConnectionResponse) {
                    ConnectionResponse response = (ConnectionResponse) object;
                    connectionAccepted = response.connectionAccepted;
                } else if (object instanceof LobbyResponse) {
                    LobbyResponse response = (LobbyResponse) object;
                    matchRoleList = new ArrayList<>(response.matchRoleList);
                } else if (object instanceof AssignRoleResponse) {
                    AssignRoleResponse response = (AssignRoleResponse) object;
                    assignedRole = Role.getRole(response.assignedRole);
                } else if (object instanceof ProceedResponse) {
                    ProceedResponse response = (ProceedResponse) object;
                    permitted = response.permit;
                    alivePlayersMap = response.alivePlayerMap;
                    deadPlayersMap = response.deadPlayerMap;
                    if (response.hangedList != null) {
                        hangedList = response.hangedList;
                    }
                } else if (object instanceof VoteResponse) {
                    VoteResponse response = (VoteResponse) object;
                    DayScreen.updateVote(response.votedPlayerName, response.vote);
                }
            }
        });
    }
}
