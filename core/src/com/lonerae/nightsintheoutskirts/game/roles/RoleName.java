package com.lonerae.nightsintheoutskirts.game.roles;

public enum RoleName {
    ASSASSIN("Assassin"), CIVILIAN("Civilian"), FOURTH_CIVILIAN("the Fourth Civilian"), HERMIT("Hermit"), NECROMANCER("Necromancer"), WITCH("Witch");

    private final String toString;

    RoleName(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }
}
