package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.AllianceName;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.responses.AssignRoleResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ConnectionResponse;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;
import com.lonerae.nightsintheoutskirts.network.responses.LobbyResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ProceedResponse;
import com.lonerae.nightsintheoutskirts.network.responses.VoteResponse;
import com.lonerae.nightsintheoutskirts.network.responses.abilities.AssassinInfoResponse;
import com.lonerae.nightsintheoutskirts.network.responses.abilities.MurderResponse;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.DayScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.AssassinNightScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchClient {

    private static MatchClient matchClientInstance = null;

    private final Map<String, Integer> availableMatches = new HashMap<>();
    private Client client;
    private List<RoleName> matchRoleList = null;
    private Boolean connectionAccepted = null;
    private Role assignedRole = null;
    private Boolean permitted = null;
    private Boolean assassinPermitted = null;

    private boolean firstFlag = true;
    private HashMap<String, RoleName> connectedPlayersMap = null;
    private HashMap<String, RoleName> alivePlayersMap = null;
    private HashMap<String, RoleName> deadPlayersMap = null;
    private List<String> hangedList = null;
    private List<String> murderedList = null;

    private Boolean endGame = null;
    private AllianceName winner = null;

    private MatchClient(){
        client = new Client();
        client.start();
        NetworkUtil.register(client);
        createListener();
    }

    /**
     * Lazy Initialisation Singleton
     */
    public static MatchClient getMatchClientInstance() {
        if (matchClientInstance == null) {
            matchClientInstance = new MatchClient();
        }
        return matchClientInstance;
    }

    public Client getClient() {
        return client;
    }

    public void close() {
        client.stop();
        clearClient();
    }

    private void clearClient() {
        client = null;
        availableMatches.clear();
        matchRoleList = null;
        connectionAccepted = null;
        assignedRole = null;
        permitted = null;
        assassinPermitted = null;
        firstFlag = true;
        connectedPlayersMap = null;
        alivePlayersMap = null;
        deadPlayersMap = null;
        hangedList = null;
        murderedList = null;
        endGame = null;
        winner = null;
        matchClientInstance = null;
    }

    public Map<String, Integer> getAvailableMatches() {
        return availableMatches;
    }

    public List<RoleName> getMatchRoleList() {
        return matchRoleList;
    }

    public Boolean isConnectionAccepted() {
        return connectionAccepted;
    }

    public Role getAssignedRole() {
        return assignedRole;
    }

    public Boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(Boolean permitted) {
        this.permitted = permitted;
    }

    public Boolean getAssassinPermitted() {
        return assassinPermitted;
    }

    public void setAssassinPermitted(Boolean assassinPermitted) {
        this.assassinPermitted = assassinPermitted;
    }

    public HashMap<String, RoleName> getConnectedPlayersMap() {
        return connectedPlayersMap;
    }

    public HashMap<String, RoleName> getAlivePlayersMap() {
        return alivePlayersMap;
    }

    public HashMap<String, RoleName> getDeadPlayersMap() {
        return deadPlayersMap;
    }

    public List<String> getHangedList() {
        return hangedList;
    }

    public List<String> getMurderedList() {
        return murderedList;
    }

    public void setEndGame(Boolean endGame) {
        this.endGame = endGame;
    }

    public Boolean isEndGame() {
        return endGame;
    }

    public AllianceName getWinner() {
        return winner;
    }

    private void createListener() {
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
                    firstConnection(response);
                    updateLists(response);
                    checkEndGame(response);
                } else if (object instanceof VoteResponse) {
                    VoteResponse response = (VoteResponse) object;
                    DayScreen.updateVote(response.voterName, response.votedPlayerName, response.vote);
                } else if (object instanceof AssassinInfoResponse) {
                    if (Player.getPlayer().getRole().getName().equals(RoleName.ASSASSIN)) {
                        AssassinInfoResponse response = (AssassinInfoResponse) object;
                        AssassinNightScreen.updateOverview(response.killer, response.target, response.skip);
                    }
                } else if (object instanceof MurderResponse) {
                    if (Player.getPlayer().getRole().getName().equals(RoleName.ASSASSIN)) {
                        MurderResponse response = (MurderResponse) object;
                        assassinPermitted = response.permit;
                    }
                }
            }
        });
    }

    private void firstConnection(ProceedResponse response) {
        if (firstFlag) {
            connectedPlayersMap = response.alivePlayerMap;
            firstFlag = false;
        }
    }

    private void checkEndGame(ProceedResponse response) {
        if (response.endGame) {
            endGame = true;
            winner = response.winner;
        } else {
            endGame = false;
        }
    }

    private void updateLists(ProceedResponse response) {
        if (response.alivePlayerMap != null) {
            alivePlayersMap = response.alivePlayerMap;
        }
        if (response.deadPlayerMap != null) {
            deadPlayersMap = response.deadPlayerMap;
        }
        if (response.hangedList != null) {
            hangedList = response.hangedList;
        }
        if (response.murderedList != null) {
            murderedList = response.murderedList;
        }
    }
}
