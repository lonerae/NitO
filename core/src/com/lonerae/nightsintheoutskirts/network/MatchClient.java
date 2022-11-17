package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.responses.AssignRoleResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ConnectionResponse;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;
import com.lonerae.nightsintheoutskirts.network.responses.LobbyResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchClient {

    private static Client client;
    private static final Map<String, Integer> availableMatches = new HashMap<>();

    private static List<RoleName> matchRoleList;
    private static boolean connectionAccepted;
    private static Role assignedRole;

    public static void createClient() {
        if (client == null) {
            client = new Client();
            client.start();
            NetworkUtil.register(client);
            createListener();
        }
    }

    public static Client getClient() {
        return client;
    }

    public static Map<String, Integer> getAvailableMatches() {
        return availableMatches;
    }

    public static List<RoleName> getMatchRoleList() {
        return matchRoleList;
    }

    public static boolean isConnectionAccepted() {
        return connectionAccepted;
    }

    public static Role getAssignedRole() {
        return assignedRole;
    }

    private static void createListener() {
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
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
                }
            }
        });
    }
}
