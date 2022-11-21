package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class NetworkUtil {

    public static void register(Server server) {
        Kryo kryo = server.getKryo();
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
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(RoleName.class);
    }

    public static void register(Client client) {
        Kryo kryo = client.getKryo();
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
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(RoleName.class);
    }
}
