package com.lonerae.nightsintheoutskirts.network.responses;

import com.lonerae.nightsintheoutskirts.game.roles.RoleName;

import java.util.HashMap;
import java.util.Map;

public class ProceedResponse {
    public boolean permit;
    public HashMap<String, RoleName> playerMap;
}
