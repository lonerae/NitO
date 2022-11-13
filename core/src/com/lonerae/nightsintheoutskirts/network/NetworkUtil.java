package com.lonerae.nightsintheoutskirts.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.network.responses.GreetingResponse;

public class NetworkUtil {

    public static void register(Server server) {
        Kryo kryo = server.getKryo();
        kryo.register(GreetingRequest.class);
        kryo.register(GreetingResponse.class);
    }

    public static void register(Client client) {
        Kryo kryo = client.getKryo();
        kryo.register(GreetingRequest.class);
        kryo.register(GreetingResponse.class);
    }
}
