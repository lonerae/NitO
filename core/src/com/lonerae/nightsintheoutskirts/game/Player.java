package com.lonerae.nightsintheoutskirts.game;

import com.badlogic.gdx.Game;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.NightScreen;

public class Player {

    private static Player player;

    private String name;
    private Role role;
    private boolean isAlive = true;
    private boolean isAbleToUseAbility = true;

    public static Player getPlayer() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    public String getName() {
        return player.name;
    }

    public void setName(String name) {
        player.name = name;
    }

    public Role getRole() {
        return player.role;
    }

    public void setRole(Role role) {
        player.role = role;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isAbleToUseAbility() {
        return isAbleToUseAbility;
    }

    public void setAbleToUseAbility(boolean ableToUseAbility) {
        this.isAbleToUseAbility = ableToUseAbility;
    }

    public NightScreen getNight(Game game) {
        return role.getNight(role.getName(), game);
    }
}
