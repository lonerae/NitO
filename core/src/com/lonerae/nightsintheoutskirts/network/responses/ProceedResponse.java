package com.lonerae.nightsintheoutskirts.network.responses;

import com.lonerae.nightsintheoutskirts.game.roles.AllianceName;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;

import java.util.HashMap;
import java.util.List;

public class ProceedResponse {
    public boolean permit = false;
    public HashMap<String, RoleName> alivePlayerMap = null;
    public HashMap<String, RoleName> deadPlayerMap = null;

    public List<String> hangedList = null;
    public List<String> murderedList = null;

    public boolean endGame = false;
    public AllianceName winner = null;
}
