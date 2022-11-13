package com.lonerae.nightsintheoutskirts.game.roles;

import com.badlogic.gdx.graphics.Texture;

public abstract class Role {

    private String description;
    private Texture icon;
    private RoleName name;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Texture getIcon() {
        return icon;
    }

    public void setIcon(Texture icon) {
        this.icon = icon;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public static Role getRole(RoleName roleName) {
        Role role;
        switch (roleName) {
            case CIVILIAN:
                role = new Civilian();
                break;
            case ASSASSIN:
                role = new Assassin();
                break;
            case WITCH:
                role = new Witch();
                break;
            case HERMIT:
                role = new Hermit();
                break;
            default:
                role = null;
                break;
        }

        return role;
    }
}

