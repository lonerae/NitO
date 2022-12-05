package com.lonerae.nightsintheoutskirts.network.responses;

import com.lonerae.nightsintheoutskirts.game.roles.RoleName;

import java.util.HashMap;
import java.util.List;

public class ProceedResponse {
    public boolean permit;
    public HashMap<String, RoleName> alivePlayerMap;
    public HashMap<String, RoleName> deadPlayerMap;

    public List<String> hangedList;
}
