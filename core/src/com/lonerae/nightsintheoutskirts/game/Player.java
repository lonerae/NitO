package com.lonerae.nightsintheoutskirts.game;

import com.lonerae.nightsintheoutskirts.game.roles.Role;

public class Player {

    private static Player player;

    private String name;
    private Role role;
    private boolean isAlive = true;

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
}
