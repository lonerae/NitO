package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.lonerae.nightsintheoutskirts.game.roles.AllianceName;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.requests.AssignRoleRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ConnectionRequest;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.network.requests.LobbyRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
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

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkUtil {

    public static void register(Server server) {
        Kryo kryo = server.getKryo();
        register(kryo);
    }

    public static void register(Client client) {
        Kryo kryo = client.getKryo();
        register(kryo);
    }

    private static void register(Kryo kryo) {
        kryo.register(AssignRoleRequest.class);
        kryo.register(AssignRoleResponse.class);
        kryo.register(ConnectionRequest.class);
        kryo.register(ConnectionResponse.class);
        kryo.register(GreetingRequest.class);
        kryo.register(GreetingResponse.class);
        kryo.register(LobbyRequest.class);
        kryo.register(LobbyResponse.class);
        kryo.register(ProceedRequest.class);
        kryo.register(ProceedResponse.class);
        kryo.register(ProceedType.class);
        kryo.register(AllianceName.class);
        kryo.register(VoteRequest.class);
        kryo.register(VoteResponse.class);
        kryo.register(KillRequest.class);
        kryo.register(SaveRequest.class);
        kryo.register(MurderRequest.class);
        kryo.register(MurderResponse.class);
        kryo.register(AssassinInfoResponse.class);
        kryo.register(FourthRequest.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(RoleName.class);
    }
}
