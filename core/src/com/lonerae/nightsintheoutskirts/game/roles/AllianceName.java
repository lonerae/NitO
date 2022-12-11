package com.lonerae.nightsintheoutskirts.game.roles;

public enum AllianceName {
    ORDER("Order"), CHAOS("Chaos");

    private final String toString;

    AllianceName(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }
}
