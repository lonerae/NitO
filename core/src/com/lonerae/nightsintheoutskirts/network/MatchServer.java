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
import com.lonerae.nightsintheoutskirts.network.requests.VoteRequest;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.KillRequest;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.MurderRequest;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.SaveRequest;
import com.lonerae.nightsintheoutskirts.network.responses.AssignRoleResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ConnectionResponse;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;
import com.lonerae.nightsintheoutskirts.network.responses.LobbyResponse;
import com.lonerae.nightsintheoutskirts.network.responses.ProceedResponse;
import com.lonerae.nightsintheoutskirts.network.responses.VoteResponse;
import com.lonerae.nightsintheoutskirts.network.responses.abilities.AssassinInfoResponse;
import com.lonerae.nightsintheoutskirts.network.responses.abilities.MurderResponse;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MatchServer {

    private static final HashMap<String, RoleName> connectedPlayersMap = new HashMap<>();
    private static final HashMap<String, RoleName> alivePlayersMap = new HashMap<>();
    private static final HashMap<String, RoleName> deadPlayersMap = new HashMap<>();
    private static final HashMap<String, Integer> votingMap = new HashMap<>();
    private static final List<String> protectedPlayersList = new ArrayList<>();
    private static final List<String> murderedPlayersList = new ArrayList<>();
    private static Server server;
    private static GameData match;
    private static List<RoleName> shuffledDeck;

    private static int assassinNumber;
    private static int assassinSent = 0;
    private static int assassinPass = 0;
    private static String assassinTarget = null;
    private static int assassinsAgree = 0;

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
            for (int i = 0; i < match.getMatchRoleList().get(roleName); i++) {
                deck.add(roleName);
            }
        }
        Collections.shuffle(deck);
        shuffledDeck = deck;
    }

    private static void createListener() {
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
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
                        alivePlayersMap.put(request.playerName, response.assignedRole);
                        assignedPlayerNumber++;
                    }
                } else if (object instanceof ProceedRequest) {
                    ProceedRequest request = (ProceedRequest) object;
                    readyPlayerNumber++;
                    switch (request.type) {
                        case FIRST:
                            if (readyPlayerNumber == match.getNumberOfPlayers()) {
                                ProceedResponse response = new ProceedResponse();

                                response.permit = true;
                                response.alivePlayerMap = alivePlayersMap;
                                server.sendToAllTCP(response);
                                readyPlayerNumber = 0;
                            }
                            break;
                        case NORMAL:
                            if (readyPlayerNumber == alivePlayersMap.size()) {
                                assassinNumber = 0;
                                for (String player : alivePlayersMap.keySet()) {
                                    if (alivePlayersMap.get(player).equals(RoleName.ASSASSIN)) {
                                        assassinNumber++;
                                    }
                                }

                                ProceedResponse response = new ProceedResponse();
                                response.permit = true;
                                response.alivePlayerMap = alivePlayersMap;
                                server.sendToAllTCP(response);
                                readyPlayerNumber = 0;
                            }
                            break;
                        case VOTING:
                            if (readyPlayerNumber == alivePlayersMap.size()) {
                                ProceedResponse response = new ProceedResponse();

                                response.permit = true;
                                response.alivePlayerMap = alivePlayersMap;
                                response.deadPlayerMap = deadPlayersMap;
                                response.hangedList = getHanged();
                                server.sendToAllTCP(response);
                                readyPlayerNumber = 0;
                                protectedPlayersList.clear();
                            }
                            break;
                        case ABILITY:
                            if (readyPlayerNumber == alivePlayersMap.size()) {

                                for (String player : murderedPlayersList) {
                                    deadPlayersMap.put(player, alivePlayersMap.remove(player));
                                }
                                ProceedResponse response = new ProceedResponse();

                                response.permit = true;
                                response.alivePlayerMap = alivePlayersMap;
                                response.deadPlayerMap = deadPlayersMap;
                                response.murderedList = murderedPlayersList;
                                server.sendToAllTCP(response);
                                readyPlayerNumber = 0;
                                protectedPlayersList.clear();
                                murderedPlayersList.clear();
                            }
                            break;
                    }
                } else if (object instanceof VoteRequest) {
                    VoteRequest request = (VoteRequest) object;
                    String voterName = request.voterName;
                    String votedPlayerName = request.votedPlayerName;
                    int newVote = request.vote;
                    if (!votingMap.containsKey(votedPlayerName)) {
                        votingMap.put(votedPlayerName, newVote);
                    } else {
                        int oldVote = votingMap.get(votedPlayerName);
                        votingMap.put(votedPlayerName, oldVote + newVote);
                    }
                    VoteResponse response = new VoteResponse();
                    response.voterName = voterName;
                    response.votedPlayerName = votedPlayerName;
                    response.vote = votingMap.get(votedPlayerName);
                    server.sendToAllTCP(response);
                } else if (object instanceof KillRequest) {
                    KillRequest request = (KillRequest) object;
                    String deadPlayer = request.playerName;
                    if (!protectedPlayersList.contains(deadPlayer) && !murderedPlayersList.contains(deadPlayer)) {
                        murderedPlayersList.add(deadPlayer);
                    }
                } else if (object instanceof SaveRequest) {
                    SaveRequest request = (SaveRequest) object;
                    String protectedPlayer = request.playerName;
                    if (!protectedPlayersList.contains(protectedPlayer)) {
                        protectedPlayersList.add(protectedPlayer);
                    }
                    murderedPlayersList.remove(protectedPlayer);
                } else if (object instanceof MurderRequest) {
                    MurderRequest request = (MurderRequest) object;
                    AssassinInfoResponse info = new AssassinInfoResponse();

                    if (request.willKill) {
                        info.skip = false;
                        info.killer = request.killer;
                        info.target = request.target;
                        if (assassinTarget == null || request.target.equals(assassinTarget)) {
                            assassinsAgree++;
                            assassinTarget = request.target;
                        }
                    } else {
                        info.skip = true;
                        info.killer = request.killer;
                        assassinPass++;
                    }

                    server.sendToAllTCP(info);
                    assassinSent++;
                    if (assassinSent == assassinNumber) {
                        MurderResponse response = new MurderResponse();

                        if (assassinPass < assassinNumber || assassinsAgree < assassinNumber) {
                            response.permit = false;
                        }
                        if (assassinPass == assassinNumber) {
                            response.permit = true;
                        }
                        if (assassinsAgree == assassinNumber) {
                            response.permit = true;
                            if (!murderedPlayersList.contains(assassinTarget)) {
                                murderedPlayersList.add(assassinTarget);
                            }
                        }

                        server.sendToAllTCP(response);
                        assassinSent = 0;
                        assassinTarget = null;
                        assassinsAgree = 0;
                        assassinPass = 0;
                    }
                }
            }
        });
    }

    private static List<String> getHanged() {
        List<String> hangedList = new ArrayList<>();
        try {
            int maxVotes = Collections.max(votingMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
            for (String playerName : votingMap.keySet()) {
                if (votingMap.get(playerName) == maxVotes) {
                    hangedList.add(playerName);
                    deadPlayersMap.put(playerName, alivePlayersMap.remove(playerName));
                }
            }
        } catch (NoSuchElementException ignored) {
        }
        return hangedList;
    }
}
