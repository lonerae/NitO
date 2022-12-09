package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.roles.AllianceName;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    private static int connectedPlayersNumber = 0;
    private static int assignedPlayerNumber = 0;
    private static int readyPlayerNumber = 0;

    private static int assassinNumber;
    private static int assassinSent = 0;
    private static int assassinPass = 0;
    private static String assassinTarget = null;
    private static int assassinsAgree = 0;

    private static AllianceName winner;

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

    public static Server getServer() {
        return server;
    }

    public static void close() {
        server.stop();
        clearServer();
    }

    private static void clearServer() {
        server = null;
        connectedPlayersMap.clear();
        alivePlayersMap.clear();
        deadPlayersMap.clear();
        votingMap.clear();
        protectedPlayersList.clear();
        murderedPlayersList.clear();
        match = null;
        shuffledDeck = null;
        connectedPlayersNumber = 0;
        assignedPlayerNumber = 0;
        readyPlayerNumber = 0;
        assassinNumber = 0;
        assassinSent = 0;
        assassinPass = 0;
        assassinTarget = null;
        assassinsAgree = 0;
        winner = null;
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
                    revealMatchInfo(connection);
                } else if (object instanceof ConnectionRequest) {
                    checkAndAcceptConnection(connection);
                } else if (object instanceof LobbyRequest) {
                    sendMatchRoleList(connection);
                } else if (object instanceof AssignRoleRequest) {
                    assignRole(connection, (AssignRoleRequest) object);
                } else if (object instanceof ProceedRequest) {
                    checkAndProceed((ProceedRequest) object);
                } else if (object instanceof VoteRequest) {
                    updateVotesAndSend((VoteRequest) object);
                } else if (object instanceof KillRequest) {
                    kill((KillRequest) object);
                } else if (object instanceof SaveRequest) {
                    protect((SaveRequest) object);
                } else if (object instanceof MurderRequest) {
                    updateAndInformAssassins((MurderRequest) object);
                }
            }
        });
    }

    private static void updateAndInformAssassins(MurderRequest object) {
        AssassinInfoResponse info = new AssassinInfoResponse();

        if (object.willKill) {
            info.skip = false;
            info.killer = object.killer;
            info.target = object.target;
            if (assassinTarget == null || object.target.equals(assassinTarget)) {
                assassinsAgree++;
                assassinTarget = object.target;
            }
        } else {
            info.skip = true;
            info.killer = object.killer;
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

    private static void protect(SaveRequest object) {
        String protectedPlayer = object.playerName;
        if (!protectedPlayersList.contains(protectedPlayer)) {
            protectedPlayersList.add(protectedPlayer);
        }
        murderedPlayersList.remove(protectedPlayer);
    }

    private static void kill(KillRequest object) {
        String deadPlayer = object.playerName;
        if (!protectedPlayersList.contains(deadPlayer) && !murderedPlayersList.contains(deadPlayer)) {
            murderedPlayersList.add(deadPlayer);
        }
    }

    private static void updateVotesAndSend(VoteRequest object) {
        String voterName = object.voterName;
        String votedPlayerName = object.votedPlayerName;
        int newVote = object.vote;
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
    }

    private static void checkAndProceed(ProceedRequest object) {
        readyPlayerNumber++;
        switch (object.type) {
            case FIRST: //AFTER FIRST NIGHT
                proceedToFirstNight();
                break;
            case NORMAL:
                proceed();
                break;
            case VOTING: //AFTER DAY
                proceedToDayResolution();
                break;
            case ABILITY: //AFTER NIGHT
                proceedToNightResolution();
                break;
            case END: //AFTER NIGHT RESOLUTION
                proceedToDay();
                break;
        }
    }

    private static void proceedToDay() {
        if (readyPlayerNumber == alivePlayersMap.size()) {
            ProceedResponse response = new ProceedResponse();
            updateAssassinNumber();
            if (checkEndGameConditions()) {
                response.endGame = true;
                response.winner = winner;
            }

            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            server.sendToAllTCP(response);
            readyPlayerNumber = 0;
        }
    }

    private static boolean checkEndGameConditions() {
        if (checkDraw()) return true;
        if (checkAllianceEndGame()) return true;
        if (checkOrderEndGame()) return true;
        return checkDualEndGame();
    }

    private static boolean checkDualEndGame() {
        Collection<RoleName> aliveRoleNames = alivePlayersMap.values();
        if (aliveRoleNames.size() == 2) {
            if (aliveRoleNames.contains(RoleName.ASSASSIN)) {
                if (aliveRoleNames.contains(RoleName.CIVILIAN)) {
                    winner = AllianceName.ORDER;
                } else {
                    winner = AllianceName.CHAOS;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean checkOrderEndGame() {
        if (assassinNumber == 0) {
            winner = AllianceName.ORDER;
            return true;
        }
        return false;
    }

    private static boolean checkAllianceEndGame() {
        List<AllianceName> allianceList = alivePlayersMap.values()
                .stream()
                .map(Role::getRole)
                .map(Role::getAlliance)
                .distinct()
                .collect(Collectors.toList());
        if (allianceList.size() == 1) {
            winner = allianceList.get(0);
            return true;
        }
        return false;
    }

    private static boolean checkDraw() {
        if (alivePlayersMap.keySet().size() == 0) {
            winner = null;
            return true;
        }
        return false;
    }

    private static void updateAssassinNumber() {
        assassinNumber = 0;
        for (String player : alivePlayersMap.keySet()) {
            if (alivePlayersMap.get(player).equals(RoleName.ASSASSIN)) {
                assassinNumber++;
            }
        }
    }

    private static void proceedToNightResolution() {
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
    }

    private static void proceedToDayResolution() {
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
    }

    private static void proceed() {
        if (readyPlayerNumber == alivePlayersMap.size()) {
            ProceedResponse response = new ProceedResponse();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            server.sendToAllTCP(response);
            readyPlayerNumber = 0;
        }
    }

    private static void proceedToFirstNight() {
        if (readyPlayerNumber == match.getNumberOfPlayers()) {
            ProceedResponse response = new ProceedResponse();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            server.sendToAllTCP(response);
            readyPlayerNumber = 0;
        }
    }

    private static void assignRole(Connection connection, AssignRoleRequest object) {
        if (!connectedPlayersMap.containsKey(object.playerName)) {
            AssignRoleResponse response = new AssignRoleResponse();
            response.assignedRole = shuffledDeck.get(assignedPlayerNumber);
            connection.sendTCP(response);
            connectedPlayersMap.put(object.playerName, response.assignedRole);
            alivePlayersMap.put(object.playerName, response.assignedRole);
            assignedPlayerNumber++;
        }
    }

    private static void sendMatchRoleList(Connection connection) {
        LobbyResponse response = new LobbyResponse();
        response.matchRoleList = new ArrayList<>(match.getMatchRoleList().keySet());
        connection.sendTCP(response);
    }

    private static void checkAndAcceptConnection(Connection connection) {
        ConnectionResponse response = new ConnectionResponse();
        if (connectedPlayersNumber < match.getNumberOfPlayers()) {
            connectedPlayersNumber++;
        }
        response.connectionAccepted = connectedPlayersNumber < match.getNumberOfPlayers();
        connection.sendTCP(response);
    }

    private static void revealMatchInfo(Connection connection) {
        GreetingResponse response = new GreetingResponse();
        response.townName = match.getTownName();
        response.numberOfPlayers = match.getNumberOfPlayers();
        connection.sendTCP(response);
    }

    private static List<String> getHanged() {
        List<String> hangedList = new ArrayList<>();
        try {
            int maxVotes = Collections.max(votingMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
            if (maxVotes > 0) {
                for (String playerName : votingMap.keySet()) {
                    if (votingMap.get(playerName) == maxVotes) {
                        hangedList.add(playerName);
                        deadPlayersMap.put(playerName, alivePlayersMap.remove(playerName));
                    }
                }
            }
        } catch (NoSuchElementException ignored) {
        }
        return hangedList;
    }
}
