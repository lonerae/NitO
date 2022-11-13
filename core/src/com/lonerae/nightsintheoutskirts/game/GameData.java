package com.lonerae.nightsintheoutskirts.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameData {

    private static final I18NBundle roleStrings = I18NBundle.createBundle(Gdx.files.internal("strings/roleStrings"));
    private static final Map<RoleName, Role> roleList = new LinkedHashMap<>();

    private final String townName;
    private final int numberOfPlayers;
    private final Map<RoleName, Integer> matchRoleList;

    static {
        for (RoleName roleName : RoleName.values()) {
            roleList.put(roleName, Role.getRole(roleName));
        }
    }

    public GameData(String townName, int numberOfPlayers, Map<RoleName, Integer> matchRoleList) {
        this.townName = townName;
        this.numberOfPlayers = numberOfPlayers;
        this.matchRoleList = matchRoleList;
    }

    public static Map<RoleName, Role> getRoleList() {
        return new LinkedHashMap<>(roleList);
    }

    public static I18NBundle getRoleStrings() {
        return roleStrings;
    }

    public Map<RoleName, Integer> getMatchRoleList() {
        return matchRoleList;
    }

    public String getTownName() {
        return townName;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
