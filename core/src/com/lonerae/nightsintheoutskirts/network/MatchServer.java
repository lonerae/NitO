package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
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
import com.lonerae.nightsintheoutskirts.network.requests.abilities.FourthRequest;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MatchServer {

    private static MatchServer matchServerInstance;

    private final HashMap<String, RoleName> connectedPlayersMap = new HashMap<>();
    private final HashMap<String, RoleName> alivePlayersMap = new HashMap<>();
    private final HashMap<String, RoleName> deadPlayersMap = new HashMap<>();
    private final HashMap<String, Integer> votingMap = new HashMap<>();
    private final List<String> protectedPlayersList = new ArrayList<>();
    private final List<String> murderedPlayersList = new ArrayList<>();
    private final List<String> fourthTransformations = new ArrayList<>();
    private Server server;
    private GameData match;
    private List<RoleName> shuffledDeck;

    private AtomicInteger connectedPlayersNumber = new AtomicInteger(0);
    private AtomicInteger assignedPlayerNumber = new AtomicInteger(0);
    private AtomicInteger readyPlayerNumber = new AtomicInteger(0);

    private AtomicInteger assassinNumber = new AtomicInteger(0);
    private AtomicInteger assassinSent = new AtomicInteger(0);
    private AtomicInteger assassinPass = new AtomicInteger(0);
    private String assassinTarget = null;
    private AtomicInteger assassinsAgree = new AtomicInteger(0);

    private AllianceName winner;

    private static final Object lock = new Object();

    private MatchServer(){}

    /**
     * Lazy Initialisation Singleton
     */
    public static MatchServer getMatchServerInstance() {
        if (matchServerInstance == null) {
            matchServerInstance = new MatchServer();
        }
        return matchServerInstance;
    }

    public void createServer(GameData data) throws UnknownHostException {
        if (server == null) {
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

    public Server getServer() {
        return server;
    }

    public void close() {
        server.stop();
        clearServer();
    }

    private void clearServer() {
        server = null;
        connectedPlayersMap.clear();
        alivePlayersMap.clear();
        deadPlayersMap.clear();
        votingMap.clear();
        protectedPlayersList.clear();
        murderedPlayersList.clear();
        fourthTransformations.clear();
        match = null;
        shuffledDeck = null;
        connectedPlayersNumber = new AtomicInteger(0);
        assignedPlayerNumber = new AtomicInteger(0);
        readyPlayerNumber = new AtomicInteger(0);
        assassinNumber = new AtomicInteger(0);
        assassinSent = new AtomicInteger(0);
        assassinPass = new AtomicInteger(0);
        assassinTarget = null;
        assassinsAgree = new AtomicInteger(0);
        winner = null;
    }

    private void shuffleDeck() {
        List<RoleName> deck = new ArrayList<>();
        for (RoleName roleName : match.getMatchRoleList().keySet()) {
            for (int i = 0; i < match.getMatchRoleList().get(roleName); i++) {
                deck.add(roleName);
            }
        }
        Collections.shuffle(deck);
        shuffledDeck = deck;
    }

    private void createListener() {
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
                } else if (object instanceof FourthRequest) {
                    FourthRequest request = (FourthRequest) object;
                    fourthTransformations.add(request.playerName);
                }
            }
        });
    }

    private void revealMatchInfo(Connection connection) {
        GreetingResponse response = new GreetingResponse();
        response.townName = match.getTownName();
        response.numberOfPlayers = match.getNumberOfPlayers();
        connection.sendTCP(response);
    }

    private void checkAndAcceptConnection(Connection connection) {
        ConnectionResponse response = new ConnectionResponse();
        if (connectedPlayersNumber.get() < match.getNumberOfPlayers()) {
                connectedPlayersNumber.incrementAndGet();
        }
        response.connectionAccepted = connectedPlayersNumber.get() < match.getNumberOfPlayers();
        connection.sendTCP(response);
    }

    private void sendMatchRoleList(Connection connection) {
        LobbyResponse response = new LobbyResponse();
        response.matchRoleList = new ArrayList<>(match.getMatchRoleList().keySet());
        connection.sendTCP(response);
    }

    private void assignRole(Connection connection, AssignRoleRequest object) {
        if (!connectedPlayersMap.containsKey(object.playerName)) {
            AssignRoleResponse response = new AssignRoleResponse();
            response.assignedRole = shuffledDeck.get(assignedPlayerNumber.get());
            connection.sendTCP(response);
            connectedPlayersMap.put(object.playerName, response.assignedRole);
            alivePlayersMap.put(object.playerName, response.assignedRole);
            assignedPlayerNumber.incrementAndGet();
        }
    }

    private void checkAndProceed(ProceedRequest object) {
        readyPlayerNumber.incrementAndGet();
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
            case END: //AFTER NIGHT & DAY RESOLUTION
                proceedToNextPhase();
                break;
        }
    }

    private void proceedToFirstNight() {
        if (readyPlayerNumber.get() == match.getNumberOfPlayers()) {
            ProceedResponse response = new ProceedResponse();
            updateAssassinNumber();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            if (checkEndGameConditions()) {
                response.endGame = true;
                response.winner = winner;
            }
            server.sendToAllTCP(response);
            readyPlayerNumber = new AtomicInteger(0);

        }
    }

    private void proceed() {
        if (readyPlayerNumber.get() == alivePlayersMap.size()) {
            ProceedResponse response = new ProceedResponse();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            server.sendToAllTCP(response);
            readyPlayerNumber = new AtomicInteger(0);

        }
    }

    private void proceedToDayResolution() {
        if (readyPlayerNumber.get() == alivePlayersMap.size()) {
            ProceedResponse response = new ProceedResponse();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            response.deadPlayerMap = deadPlayersMap;
            response.hangedList = getHanged();
            server.sendToAllTCP(response);
            readyPlayerNumber = new AtomicInteger(0);
            protectedPlayersList.clear();
            votingMap.clear();

        }
    }

    private List<String> getHanged() {
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

    private void proceedToNightResolution() {
        if (readyPlayerNumber.get() == alivePlayersMap.size()) {
            for (String player : murderedPlayersList) {
                deadPlayersMap.put(player, alivePlayersMap.remove(player));
            }
            fourthCivilianCheck();

            ProceedResponse response = new ProceedResponse();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            response.deadPlayerMap = deadPlayersMap;
            response.murderedList = murderedPlayersList;
            server.sendToAllTCP(response);
            readyPlayerNumber = new AtomicInteger(0);
            protectedPlayersList.clear();
            murderedPlayersList.clear();

        }
    }

    private void fourthCivilianCheck() {
        if (!fourthTransformations.isEmpty()) {
            List<RoleName> availableRoles = deadPlayersMap.keySet()
                    .stream()
                    .filter(murderedPlayersList::contains)
                    .map(deadPlayersMap::get)
                    .sorted()
                    .collect(Collectors.toList());
            int count = 0;
            for (String player : fourthTransformations) {
                if (count == availableRoles.size()) {
                    break;
                }
                if (!deadPlayersMap.containsKey(player)) {
                    alivePlayersMap.put(player, availableRoles.get(count));
                    count++;
                }
            }
            fourthTransformations.clear();
        }
    }

    private void proceedToNextPhase() {
        if (readyPlayerNumber.get() == alivePlayersMap.size()) {
            ProceedResponse response = new ProceedResponse();
            if (checkEndGameConditions()) {
                response.endGame = true;
                response.winner = winner;
            }
            updateAssassinNumber();
            response.permit = true;
            response.alivePlayerMap = alivePlayersMap;
            server.sendToAllTCP(response);
            readyPlayerNumber = new AtomicInteger(0);

        }
    }

    private void updateAssassinNumber() {
        assassinNumber = new AtomicInteger(0);
        for (String player : alivePlayersMap.keySet()) {
            if (alivePlayersMap.get(player).equals(RoleName.ASSASSIN)) {
                assassinNumber.incrementAndGet();
            }
        }
    }

    private void updateVotesAndSend(VoteRequest object) {
        String voterName = object.voterName;
        String votedPlayerName = object.votedPlayerName;
        int newVote = object.vote;
        synchronized (lock) {
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
    }

    private void kill(KillRequest object) {
        String deadPlayer = object.playerName;
        synchronized (lock) {
            if (!protectedPlayersList.contains(deadPlayer) && !murderedPlayersList.contains(deadPlayer)) {
                murderedPlayersList.add(deadPlayer);
            }
        }
    }

    private void protect(SaveRequest object) {
        String protectedPlayer = object.playerName;
        synchronized (lock) {
            if (!protectedPlayersList.contains(protectedPlayer)) {
                protectedPlayersList.add(protectedPlayer);
            }
            murderedPlayersList.remove(protectedPlayer);
        }
    }

    private void updateAndInformAssassins(MurderRequest object) {
        AssassinInfoResponse info = new AssassinInfoResponse();

        synchronized (lock) {
            if (object.willKill) {
                info.skip = false;
                info.killer = object.killer;
                info.target = object.target;
                if (assassinTarget == null || object.target.equals(assassinTarget)) {
                    assassinsAgree.incrementAndGet();
                    assassinTarget = object.target;
                }
            } else {
                info.skip = true;
                info.killer = object.killer;
                assassinPass.incrementAndGet();
            }
        }

        server.sendToAllTCP(info);
        assassinSent.incrementAndGet();

        if (assassinSent.get() == assassinNumber.get()) {
            MurderResponse response = new MurderResponse();

            if (assassinPass.get() < assassinNumber.get() || assassinsAgree.get() < assassinNumber.get()) {
                response.permit = false;
            }
            if (assassinPass.get() == assassinNumber.get()) {
                response.permit = true;
            }
            if (assassinsAgree.get() == assassinNumber.get()) {
                response.permit = true;
                if (!murderedPlayersList.contains(assassinTarget) && !protectedPlayersList.contains(assassinTarget)) {
                    murderedPlayersList.add(assassinTarget);
                }
            }

            server.sendToAllTCP(response);
            assassinSent = new AtomicInteger(0);
            assassinTarget = null;
            assassinsAgree = new AtomicInteger(0);
            assassinPass = new AtomicInteger(0);

        }
    }

    private boolean checkEndGameConditions() {
        if (checkDraw()) return true;
        if (checkAllianceEndGame()) return true;
        if (checkOrderEndGame()) return true;
        return checkDualEndGame();
    }

    private boolean checkDualEndGame() {
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

    private boolean checkOrderEndGame() {
        if (assassinNumber.get() == 0) {
            winner = AllianceName.ORDER;
            return true;
        }
        return false;
    }

    private boolean checkAllianceEndGame() {
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

    private boolean checkDraw() {
        if (alivePlayersMap.keySet().size() == 0) {
            winner = null;
            return true;
        }
        return false;
    }
}
