package com.lonerae.nightsintheoutskirts.game.roles;

import com.badlogic.gdx.Game;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.AssassinNightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.CivilianNightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.FourthCivilianNightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.HermitNightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.NecromancerNightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.NightScreen;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night.WitchNightScreen;

public abstract class Role implements Comparable<Role> {

    private String description;
    private String iconPath;
    private RoleName name;
    private AllianceName alliance;
    private int priority;

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
            case NECROMANCER:
                role = new Necromancer();
                break;
            case FOURTH_CIVILIAN:
                role = new FourthCivilian();
                break;
            default:
                role = null;
                break;
        }

        return role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public AllianceName getAlliance() {
        return alliance;
    }

    public void setAlliance(AllianceName alliance) {
        this.alliance = alliance;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public NightScreen getNight(RoleName roleName, Game game) {
        NightScreen night;
        switch (roleName) {
            case CIVILIAN:
                night = new CivilianNightScreen(game);
                break;
            case ASSASSIN:
                night = new AssassinNightScreen(game);
                break;
            case WITCH:
                night = new WitchNightScreen(game);
                break;
            case HERMIT:
                night = new HermitNightScreen(game);
                break;
            case NECROMANCER:
                night = new NecromancerNightScreen(game);
                break;
            case FOURTH_CIVILIAN:
                night = new FourthCivilianNightScreen(game);
                break;
            default:
                night = null;
                break;
        }

        return night;
    }

    @Override
    public int compareTo(Role otherRole) {
        return this.priority - otherRole.priority;
    }
}

